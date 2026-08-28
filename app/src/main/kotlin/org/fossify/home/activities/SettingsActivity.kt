package org.fossify.home.activities

import android.annotation.SuppressLint
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import org.fossify.commons.dialogs.RadioGroupDialog
import org.fossify.commons.extensions.beVisibleIf
import org.fossify.commons.extensions.getProperPrimaryColor
import org.fossify.commons.extensions.launchMoreAppsFromUsIntent
import org.fossify.commons.extensions.updateTextColors
import org.fossify.commons.extensions.viewBinding
import org.fossify.commons.helpers.NavigationIcon
import org.fossify.commons.helpers.isTiramisuPlus
import org.fossify.commons.models.FAQItem
import org.fossify.commons.models.RadioItem
import org.fossify.home.BuildConfig
import org.fossify.home.R
import org.fossify.home.databinding.ActivitySettingsBinding
import org.fossify.home.extensions.config
import org.fossify.home.helpers.COLOR_BLIND_DEUTERANOPIA
import org.fossify.home.helpers.COLOR_BLIND_NONE
import org.fossify.home.helpers.COLOR_BLIND_PROTANOPIA
import org.fossify.home.helpers.COLOR_BLIND_TRITANOPIA
import org.fossify.home.helpers.FONT_DEFAULT
import org.fossify.home.helpers.FONT_LEXEND
import org.fossify.home.helpers.FONT_OPEN_DYSLEXIC
import org.fossify.home.helpers.ICON_LABEL_POSITION_BOTTOM
import org.fossify.home.helpers.ICON_LABEL_POSITION_HIDDEN
import org.fossify.home.helpers.ICON_LABEL_POSITION_RIGHT
import org.fossify.home.helpers.MAX_COLUMN_COUNT
import org.fossify.home.helpers.MAX_ROW_COUNT
import org.fossify.home.helpers.MIN_COLUMN_COUNT
import org.fossify.home.helpers.MIN_ROW_COUNT
import org.fossify.home.helpers.TEXT_SIZE_EXTRA_LARGE
import org.fossify.home.helpers.TEXT_SIZE_LARGE
import org.fossify.home.helpers.TEXT_SIZE_NORMAL
import org.fossify.home.helpers.TEXT_SIZE_SMALL
import org.fossify.home.receivers.LockDeviceAdminReceiver
import java.util.Locale
import kotlin.system.exitProcess

class SettingsActivity : SimpleActivity() {

    private val binding by viewBinding(ActivitySettingsBinding::inflate)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupEdgeToEdge(padBottomSystem = listOf(binding.settingsNestedScrollview))
        setupMaterialScrollListener(binding.settingsNestedScrollview, binding.settingsAppbar)
        setupOptionsMenu()
    }

    override fun onResume() {
        super.onResume()
        setupTopAppBar(binding.settingsAppbar, NavigationIcon.Arrow)
        refreshMenuItems()

        setupCustomizeColors()
        setupUseEnglish()
        setupDoubleTapToLock()
        setupCloseAppDrawerOnOtherAppOpen()
        setupOpenKeyboardOnAppDrawer()
        setupDrawerColumnCount()
        setupDrawerSearchBar()
        setupShowDrawerAppLabels()
        setupHomeRowCount()
        setupHomeColumnCount()
        setupShowHomeAppLabels()
        setupHighContrastMode()
        setupAccessibilityFont()
        setupTextSize()
        setupColorBlindMode()
        setupIconLabelPosition()
        setupHapticIntensity()
        setupOneHandedMode()
        setupGamingMode()
        setupMeetingMode()
        setupDarkModeScheduling()
        setupGradientFolderBackground()
        setupGlassmorphismUi()
        setupLanguage()
        setupManageHiddenIcons()
        updateTextColors(binding.settingsHolder)

        arrayOf(
            binding.settingsColorCustomizationSectionLabel,
            binding.settingsGeneralSettingsLabel,
            binding.settingsDrawerSettingsLabel,
            binding.settingsHomeScreenLabel,
            binding.settingsAccessibilityLabel
        ).forEach {
            it.setTextColor(getProperPrimaryColor())
        }
    }

    private fun setupOptionsMenu() {
        binding.settingsToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.about -> launchAbout()
                R.id.more_apps_from_us -> launchMoreAppsFromUsIntent()
                else -> return@setOnMenuItemClickListener false
            }
            return@setOnMenuItemClickListener true
        }
    }

    private fun refreshMenuItems() {
        binding.settingsToolbar.menu.apply {
            findItem(R.id.more_apps_from_us).isVisible =
                !resources.getBoolean(org.fossify.commons.R.bool.hide_google_relations)
        }
    }

    private fun setupCustomizeColors() {
        binding.settingsColorCustomizationHolder.setOnClickListener {
            startCustomizationActivity()
        }
    }

    private fun setupUseEnglish() {
        binding.settingsUseEnglishHolder.beVisibleIf(
            beVisible = (config.wasUseEnglishToggled || Locale.getDefault().language != "en")
                    && !isTiramisuPlus()
        )

        binding.settingsUseEnglish.isChecked = config.useEnglish
        binding.settingsUseEnglishHolder.setOnClickListener {
            binding.settingsUseEnglish.toggle()
            config.useEnglish = binding.settingsUseEnglish.isChecked
            exitProcess(0)
        }
    }

    private fun setupDoubleTapToLock() {
        val devicePolicyManager = getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager
        binding.settingsDoubleTapToLock.isChecked = devicePolicyManager.isAdminActive(
            ComponentName(this, LockDeviceAdminReceiver::class.java)
        )

        binding.settingsDoubleTapToLockHolder.setOnClickListener {
            val isLockDeviceAdminActive = devicePolicyManager.isAdminActive(
                ComponentName(this, LockDeviceAdminReceiver::class.java)
            )
            if (isLockDeviceAdminActive) {
                devicePolicyManager.removeActiveAdmin(
                    ComponentName(this, LockDeviceAdminReceiver::class.java)
                )
            } else {
                val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
                intent.putExtra(
                    DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                    ComponentName(this, LockDeviceAdminReceiver::class.java)
                )
                intent.putExtra(
                    DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    getString(R.string.lock_device_admin_hint)
                )
                startActivity(intent)
            }
        }
    }

    private fun setupOpenKeyboardOnAppDrawer() {
        binding.settingsOpenKeyboardOnAppDrawerHolder.beVisibleIf(config.showSearchBar)
        binding.settingsOpenKeyboardOnAppDrawer.isChecked = config.autoShowKeyboardInAppDrawer
        binding.settingsOpenKeyboardOnAppDrawerHolder.setOnClickListener {
            binding.settingsOpenKeyboardOnAppDrawer.toggle()
            config.autoShowKeyboardInAppDrawer = binding.settingsOpenKeyboardOnAppDrawer.isChecked
        }
    }

    private fun setupCloseAppDrawerOnOtherAppOpen() {
        binding.settingsCloseAppDrawerOnOtherApp.isChecked = config.closeAppDrawer
        binding.settingsCloseAppDrawerOnOtherAppHolder.setOnClickListener {
            binding.settingsCloseAppDrawerOnOtherApp.toggle()
            config.closeAppDrawer = binding.settingsCloseAppDrawerOnOtherApp.isChecked
        }
    }

    private fun setupDrawerColumnCount() {
        val currentColumnCount = config.drawerColumnCount
        binding.settingsDrawerColumnCount.text = currentColumnCount.toString()
        binding.settingsDrawerColumnCountHolder.setOnClickListener {
            val items = ArrayList<RadioItem>()
            for (i in 1..MAX_COLUMN_COUNT) {
                items.add(
                    RadioItem(
                        id = i,
                        title = resources.getQuantityString(
                            org.fossify.commons.R.plurals.column_counts, i, i
                        )
                    )
                )
            }

            RadioGroupDialog(this, items, currentColumnCount) {
                val newColumnCount = it as Int
                if (currentColumnCount != newColumnCount) {
                    config.drawerColumnCount = newColumnCount
                    setupDrawerColumnCount()
                }
            }
        }
    }

    private fun setupDrawerSearchBar() {
        val showSearchBar = config.showSearchBar
        binding.settingsShowSearchBar.isChecked = showSearchBar
        binding.settingsDrawerSearchHolder.setOnClickListener {
            binding.settingsShowSearchBar.toggle()
            config.showSearchBar = binding.settingsShowSearchBar.isChecked
            binding.settingsOpenKeyboardOnAppDrawerHolder.beVisibleIf(config.showSearchBar)
        }
    }

    private fun setupShowDrawerAppLabels() {
        binding.settingsShowDrawerAppLabels.isChecked = config.showDrawerAppLabels
        binding.settingsShowDrawerAppLabelsHolder.setOnClickListener {
            binding.settingsShowDrawerAppLabels.toggle()
            config.showDrawerAppLabels = binding.settingsShowDrawerAppLabels.isChecked
        }
    }

    private fun setupHomeRowCount() {
        val currentRowCount = config.homeRowCount
        binding.settingsHomeScreenRowCount.text = currentRowCount.toString()
        binding.settingsHomeScreenRowCountHolder.setOnClickListener {
            val items = ArrayList<RadioItem>()
            for (i in MIN_ROW_COUNT..MAX_ROW_COUNT) {
                items.add(
                    RadioItem(
                        id = i,
                        title = resources.getQuantityString(
                            org.fossify.commons.R.plurals.row_counts, i, i
                        )
                    )
                )
            }

            RadioGroupDialog(this, items, currentRowCount) {
                val newRowCount = it as Int
                if (currentRowCount != newRowCount) {
                    config.homeRowCount = newRowCount
                    setupHomeRowCount()
                }
            }
        }
    }

    private fun setupHomeColumnCount() {
        val currentColumnCount = config.homeColumnCount
        binding.settingsHomeScreenColumnCount.text = currentColumnCount.toString()
        binding.settingsHomeScreenColumnCountHolder.setOnClickListener {
            val items = ArrayList<RadioItem>()
            for (i in MIN_COLUMN_COUNT..MAX_COLUMN_COUNT) {
                items.add(
                    RadioItem(
                        id = i,
                        title = resources.getQuantityString(
                            org.fossify.commons.R.plurals.column_counts, i, i
                        )
                    )
                )
            }

            RadioGroupDialog(this, items, currentColumnCount) {
                val newColumnCount = it as Int
                if (currentColumnCount != newColumnCount) {
                    config.homeColumnCount = newColumnCount
                    setupHomeColumnCount()
                }
            }
        }
    }

    private fun setupShowHomeAppLabels() {
        binding.settingsShowHomeAppLabels.isChecked = config.showHomeAppLabels
        binding.settingsShowHomeAppLabelsHolder.setOnClickListener {
            binding.settingsShowHomeAppLabels.toggle()
            config.showHomeAppLabels = binding.settingsShowHomeAppLabels.isChecked
        }
    }

    private fun setupHighContrastMode() {
        binding.settingsHighContrastMode.isChecked = config.highContrastMode
        binding.settingsHighContrastModeHolder.setOnClickListener {
            binding.settingsHighContrastMode.toggle()
            config.highContrastMode = binding.settingsHighContrastMode.isChecked
        }
    }

    private fun setupAccessibilityFont() {
        binding.settingsAccessibilityFont.text = accessibilityFontLabel(config.accessibilityFont)
        binding.settingsAccessibilityFontHolder.setOnClickListener {
            val items = arrayListOf(
                RadioItem(FONT_DEFAULT, getString(R.string.font_default)),
                RadioItem(FONT_LEXEND, getString(R.string.font_lexend)),
                RadioItem(FONT_OPEN_DYSLEXIC, getString(R.string.font_open_dyslexic))
            )

            RadioGroupDialog(this, items, config.accessibilityFont) {
                val newFont = it as Int
                if (config.accessibilityFont != newFont) {
                    config.accessibilityFont = newFont
                    setupAccessibilityFont()
                }
            }
        }
    }

    private fun accessibilityFontLabel(font: Int) = getString(
        when (font) {
            FONT_LEXEND -> R.string.font_lexend
            FONT_OPEN_DYSLEXIC -> R.string.font_open_dyslexic
            else -> R.string.font_default
        }
    )

    private fun setupTextSize() {
        val currentLevel = config.textSizeLevel
        binding.settingsTextSize.text = textSizeLabel(currentLevel)
        binding.settingsTextSizeHolder.setOnClickListener {
            val items = arrayListOf(
                RadioItem(TEXT_SIZE_SMALL, getString(R.string.text_size_small)),
                RadioItem(TEXT_SIZE_NORMAL, getString(R.string.text_size_normal)),
                RadioItem(TEXT_SIZE_LARGE, getString(R.string.text_size_large)),
                RadioItem(TEXT_SIZE_EXTRA_LARGE, getString(R.string.text_size_extra_large))
            )

            RadioGroupDialog(this, items, config.textSizeLevel) {
                val newLevel = it as Int
                if (config.textSizeLevel != newLevel) {
                    config.textSizeLevel = newLevel
                    setupTextSize()
                }
            }
        }
    }

    private fun textSizeLabel(level: Int) = getString(
        when (level) {
            TEXT_SIZE_SMALL -> R.string.text_size_small
            TEXT_SIZE_LARGE -> R.string.text_size_large
            TEXT_SIZE_EXTRA_LARGE -> R.string.text_size_extra_large
            else -> R.string.text_size_normal
        }
    )

    private fun setupColorBlindMode() {
        binding.settingsColorBlindMode.text = colorBlindModeLabel(config.colorBlindMode)
        binding.settingsColorBlindModeHolder.setOnClickListener {
            val items = arrayListOf(
                RadioItem(COLOR_BLIND_NONE, getString(R.string.color_blind_none)),
                RadioItem(COLOR_BLIND_PROTANOPIA, getString(R.string.color_blind_protanopia)),
                RadioItem(COLOR_BLIND_DEUTERANOPIA, getString(R.string.color_blind_deuteranopia)),
                RadioItem(COLOR_BLIND_TRITANOPIA, getString(R.string.color_blind_tritanopia))
            )

            RadioGroupDialog(this, items, config.colorBlindMode) {
                val newMode = it as Int
                if (config.colorBlindMode != newMode) {
                    config.colorBlindMode = newMode
                    setupColorBlindMode()
                }
            }
        }
    }

    private fun colorBlindModeLabel(mode: Int) = getString(
        when (mode) {
            COLOR_BLIND_PROTANOPIA -> R.string.color_blind_protanopia
            COLOR_BLIND_DEUTERANOPIA -> R.string.color_blind_deuteranopia
            COLOR_BLIND_TRITANOPIA -> R.string.color_blind_tritanopia
            else -> R.string.color_blind_none
        }
    )

    private fun setupIconLabelPosition() {
        binding.settingsIconLabelPosition.text = iconLabelPositionLabel(config.iconLabelPosition)
        binding.settingsIconLabelPositionHolder.setOnClickListener {
            val items = arrayListOf(
                RadioItem(ICON_LABEL_POSITION_BOTTOM, getString(R.string.icon_label_position_bottom)),
                RadioItem(ICON_LABEL_POSITION_RIGHT, getString(R.string.icon_label_position_right)),
                RadioItem(ICON_LABEL_POSITION_HIDDEN, getString(R.string.icon_label_position_hidden))
            )

            RadioGroupDialog(this, items, config.iconLabelPosition) {
                val newPosition = it as Int
                if (config.iconLabelPosition != newPosition) {
                    config.iconLabelPosition = newPosition
                    setupIconLabelPosition()
                }
            }
        }
    }

    private fun iconLabelPositionLabel(position: Int) = getString(
        when (position) {
            ICON_LABEL_POSITION_RIGHT -> R.string.icon_label_position_right
            ICON_LABEL_POSITION_HIDDEN -> R.string.icon_label_position_hidden
            else -> R.string.icon_label_position_bottom
        }
    )

    // haptic intensity is stored as 0-100; the UI offers four presets that map onto that range
    private fun setupHapticIntensity() {
        binding.settingsHapticIntensity.text = hapticIntensityLabel(config.hapticFeedbackIntensity)
        binding.settingsHapticIntensityHolder.setOnClickListener {
            val items = arrayListOf(
                RadioItem(0, getString(R.string.haptic_intensity_off)),
                RadioItem(25, getString(R.string.haptic_intensity_low)),
                RadioItem(50, getString(R.string.haptic_intensity_medium)),
                RadioItem(100, getString(R.string.haptic_intensity_high))
            )

            RadioGroupDialog(this, items, closestHapticPreset(config.hapticFeedbackIntensity)) {
                val newIntensity = it as Int
                if (config.hapticFeedbackIntensity != newIntensity) {
                    config.hapticFeedbackIntensity = newIntensity
                    setupHapticIntensity()
                }
            }
        }
    }

    private fun closestHapticPreset(value: Int) = when {
        value <= 0 -> 0
        value <= 25 -> 25
        value <= 50 -> 50
        else -> 100
    }

    private fun hapticIntensityLabel(value: Int) = getString(
        when (closestHapticPreset(value)) {
            0 -> R.string.haptic_intensity_off
            25 -> R.string.haptic_intensity_low
            50 -> R.string.haptic_intensity_medium
            else -> R.string.haptic_intensity_high
        }
    )

    private fun setupOneHandedMode() {
        binding.settingsOneHandedMode.isChecked = config.oneHandedMode
        binding.settingsOneHandedModeHolder.setOnClickListener {
            binding.settingsOneHandedMode.toggle()
            config.oneHandedMode = binding.settingsOneHandedMode.isChecked
        }
    }

    private fun setupGamingMode() {
        binding.settingsGamingMode.isChecked = config.gamingMode
        binding.settingsGamingModeHolder.setOnClickListener {
            binding.settingsGamingMode.toggle()
            config.gamingMode = binding.settingsGamingMode.isChecked
        }
    }

    private fun setupMeetingMode() {
        binding.settingsMeetingMode.isChecked = config.meetingMode
        binding.settingsMeetingModeHolder.setOnClickListener {
            binding.settingsMeetingMode.toggle()
            config.meetingMode = binding.settingsMeetingMode.isChecked
        }
    }

    private fun setupDarkModeScheduling() {
        binding.settingsDarkModeScheduling.isChecked = config.darkModeScheduling
        binding.settingsDarkModeSchedulingHolder.setOnClickListener {
            binding.settingsDarkModeScheduling.toggle()
            config.darkModeScheduling = binding.settingsDarkModeScheduling.isChecked
        }
    }

    private fun setupGradientFolderBackground() {
        binding.settingsGradientFolderBackground.isChecked = config.gradientFolderBackground
        binding.settingsGradientFolderBackgroundHolder.setOnClickListener {
            binding.settingsGradientFolderBackground.toggle()
            config.gradientFolderBackground = binding.settingsGradientFolderBackground.isChecked
        }
    }

    private fun setupGlassmorphismUi() {
        binding.settingsGlassmorphismUi.isChecked = config.glassmorphismUi
        binding.settingsGlassmorphismUiHolder.setOnClickListener {
            binding.settingsGlassmorphismUi.toggle()
            config.glassmorphismUi = binding.settingsGlassmorphismUi.isChecked
        }
    }

    @SuppressLint("NewApi")
    private fun setupLanguage() {
        binding.settingsLanguage.text = Locale.getDefault().displayLanguage
        binding.settingsLanguageHolder.beVisibleIf(isTiramisuPlus())
        binding.settingsLanguageHolder.setOnClickListener {
            launchChangeAppLanguageIntent()
        }
    }

    private fun setupManageHiddenIcons() {
        binding.settingsManageHiddenIconsHolder.setOnClickListener {
            startActivity(Intent(this, HiddenIconsActivity::class.java))
        }
    }

    private fun launchAbout() {
        val licenses = 0L
        val faqItems = ArrayList<FAQItem>()

        if (!resources.getBoolean(org.fossify.commons.R.bool.hide_google_relations)) {
            faqItems.add(
                FAQItem(
                    title = org.fossify.commons.R.string.faq_2_title_commons,
                    text = org.fossify.commons.R.string.faq_2_text_commons
                )
            )
            faqItems.add(
                FAQItem(
                    title = org.fossify.commons.R.string.faq_6_title_commons,
                    text = org.fossify.commons.R.string.faq_6_text_commons
                )
            )
        }

        startAboutActivity(
            appNameId = R.string.app_name,
            licenseMask = licenses,
            versionName = BuildConfig.VERSION_NAME,
            faqItems = faqItems,
            showFAQBeforeMail = true
        )
    }
}
