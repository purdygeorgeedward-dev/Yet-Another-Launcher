package org.fossify.home.helpers

import android.content.Context
import org.fossify.commons.helpers.BaseConfig
import org.fossify.home.R

class Config(context: Context) : BaseConfig(context) {
    companion object {
        fun newInstance(context: Context) = Config(context)
    }

    var wasHomeScreenInit: Boolean
        get() = prefs.getBoolean(WAS_HOME_SCREEN_INIT, false)
        set(wasHomeScreenInit) = prefs.edit().putBoolean(WAS_HOME_SCREEN_INIT, wasHomeScreenInit).apply()

    var homeColumnCount: Int
        get() = prefs.getInt(HOME_COLUMN_COUNT, COLUMN_COUNT)
        set(homeColumnCount) = prefs.edit().putInt(HOME_COLUMN_COUNT, homeColumnCount).apply()

    var homeRowCount: Int
        get() = prefs.getInt(HOME_ROW_COUNT, ROW_COUNT)
        set(homeRowCount) = prefs.edit().putInt(HOME_ROW_COUNT, homeRowCount).apply()

    var drawerColumnCount: Int
        get() = prefs.getInt(DRAWER_COLUMN_COUNT, context.resources.getInteger(R.integer.portrait_column_count))
        set(drawerColumnCount) = prefs.edit().putInt(DRAWER_COLUMN_COUNT, drawerColumnCount).apply()

    var showSearchBar: Boolean
        get() = prefs.getBoolean(SHOW_SEARCH_BAR, true)
        set(showSearchBar) = prefs.edit().putBoolean(SHOW_SEARCH_BAR, showSearchBar).apply()

    var closeAppDrawer: Boolean
        get() = prefs.getBoolean(CLOSE_APP_DRAWER, false)
        set(closeAppDrawer) = prefs.edit().putBoolean(CLOSE_APP_DRAWER, closeAppDrawer).apply()

    var autoShowKeyboardInAppDrawer: Boolean
        get() = prefs.getBoolean(AUTO_SHOW_KEYBOARD_IN_APP_DRAWER, false)
        set(autoShowKeyboardInAppDrawer) = prefs.edit()
            .putBoolean(AUTO_SHOW_KEYBOARD_IN_APP_DRAWER, autoShowKeyboardInAppDrawer).apply()

    var showDrawerAppLabels: Boolean
        get() = prefs.getBoolean(SHOW_DRAWER_APP_LABELS, true)
        set(showDrawerAppLabels) = prefs.edit().putBoolean(SHOW_DRAWER_APP_LABELS, showDrawerAppLabels).apply()

    var showHomeAppLabels: Boolean
        get() = prefs.getBoolean(SHOW_HOME_APP_LABELS, true)
        set(showHomeAppLabels) = prefs.edit().putBoolean(SHOW_HOME_APP_LABELS, showHomeAppLabels).apply()

    // --- Tier 1 accessibility / customization preferences ---
    // Infrastructure only for now - not yet wired to a settings screen or consumed by any view.

    /** Vibration strength for icon taps/long-presses, 0-100. */
    var hapticFeedbackIntensity: Int
        get() = prefs.getInt(HAPTIC_FEEDBACK_INTENSITY, DEFAULT_HAPTIC_INTENSITY)
        set(hapticFeedbackIntensity) = prefs.edit().putInt(HAPTIC_FEEDBACK_INTENSITY, hapticFeedbackIntensity).apply()

    /** Boosts text/icon contrast against the wallpaper for low-vision users. */
    var highContrastMode: Boolean
        get() = prefs.getBoolean(HIGH_CONTRAST_MODE, false)
        set(highContrastMode) = prefs.edit().putBoolean(HIGH_CONTRAST_MODE, highContrastMode).apply()

    /** One of FONT_DEFAULT / FONT_LEXEND / FONT_OPEN_DYSLEXIC. */
    var accessibilityFont: Int
        get() = prefs.getInt(ACCESSIBILITY_FONT, FONT_DEFAULT)
        set(accessibilityFont) = prefs.edit().putInt(ACCESSIBILITY_FONT, accessibilityFont).apply()

    /** One of TEXT_SIZE_SMALL / NORMAL / LARGE / EXTRA_LARGE. */
    var textSizeLevel: Int
        get() = prefs.getInt(TEXT_SIZE_LEVEL, TEXT_SIZE_NORMAL)
        set(textSizeLevel) = prefs.edit().putInt(TEXT_SIZE_LEVEL, textSizeLevel).apply()

    /** If true, dark mode toggles automatically at the start/end times below. */
    var darkModeScheduling: Boolean
        get() = prefs.getBoolean(DARK_MODE_SCHEDULING, false)
        set(darkModeScheduling) = prefs.edit().putBoolean(DARK_MODE_SCHEDULING, darkModeScheduling).apply()

    /** Defaults to 8:00 PM - 7:00 AM. */
    var darkModeStartHour: Int
        get() = prefs.getInt(DARK_MODE_START_HOUR, 20)
        set(darkModeStartHour) = prefs.edit().putInt(DARK_MODE_START_HOUR, darkModeStartHour).apply()

    var darkModeStartMinute: Int
        get() = prefs.getInt(DARK_MODE_START_MINUTE, 0)
        set(darkModeStartMinute) = prefs.edit().putInt(DARK_MODE_START_MINUTE, darkModeStartMinute).apply()

    var darkModeEndHour: Int
        get() = prefs.getInt(DARK_MODE_END_HOUR, 7)
        set(darkModeEndHour) = prefs.edit().putInt(DARK_MODE_END_HOUR, darkModeEndHour).apply()

    var darkModeEndMinute: Int
        get() = prefs.getInt(DARK_MODE_END_MINUTE, 0)
        set(darkModeEndMinute) = prefs.edit().putInt(DARK_MODE_END_MINUTE, darkModeEndMinute).apply()

    /** Fullscreen, no notification interruptions while a game is foregrounded. */
    var gamingMode: Boolean
        get() = prefs.getBoolean(GAMING_MODE, false)
        set(gamingMode) = prefs.edit().putBoolean(GAMING_MODE, gamingMode).apply()

    /** Silences badges/notifications on the launcher without touching system DND. */
    var meetingMode: Boolean
        get() = prefs.getBoolean(MEETING_MODE, false)
        set(meetingMode) = prefs.edit().putBoolean(MEETING_MODE, meetingMode).apply()

    /** One of COLOR_BLIND_NONE / PROTANOPIA / DEUTERANOPIA / TRITANOPIA. */
    var colorBlindMode: Int
        get() = prefs.getInt(COLOR_BLIND_MODE, COLOR_BLIND_NONE)
        set(colorBlindMode) = prefs.edit().putInt(COLOR_BLIND_MODE, colorBlindMode).apply()

    /** One of ICON_LABEL_POSITION_BOTTOM / RIGHT / HIDDEN. */
    var iconLabelPosition: Int
        get() = prefs.getInt(ICON_LABEL_POSITION, ICON_LABEL_POSITION_BOTTOM)
        set(iconLabelPosition) = prefs.edit().putInt(ICON_LABEL_POSITION, iconLabelPosition).apply()

    /** Renders folder backgrounds as a gradient instead of a flat fill. */
    var gradientFolderBackground: Boolean
        get() = prefs.getBoolean(GRADIENT_FOLDER_BACKGROUND, false)
        set(gradientFolderBackground) = prefs.edit().putBoolean(GRADIENT_FOLDER_BACKGROUND, gradientFolderBackground).apply()

    /** Frosted-glass/blur styling for drawer and folder surfaces. */
    var glassmorphismUi: Boolean
        get() = prefs.getBoolean(GLASSMORPHISM_UI, false)
        set(glassmorphismUi) = prefs.edit().putBoolean(GLASSMORPHISM_UI, glassmorphismUi).apply()

    /**
     * User's stated preference for showing notification count badges on icons.
     * Deliberately opt-in (default false): turning this on is what triggers the
     * Notification access permission prompt, since badges can't work without it.
     * This flag alone doesn't mean badges are actually showing - the permission
     * also has to be granted; see Context.isNotificationBadgeAccessGranted().
     */
    var showNotificationBadges: Boolean
        get() = prefs.getBoolean(SHOW_NOTIFICATION_BADGES, false)
        set(showNotificationBadges) = prefs.edit().putBoolean(SHOW_NOTIFICATION_BADGES, showNotificationBadges).apply()
}
