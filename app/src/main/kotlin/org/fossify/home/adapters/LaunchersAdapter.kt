package org.fossify.home.adapters

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.transition.Transition
import com.qtalk.recyclerviewfastscroller.RecyclerViewFastScroller
import org.fossify.commons.extensions.beGone
import org.fossify.commons.extensions.beVisible
import org.fossify.commons.extensions.beVisibleIf
import org.fossify.commons.extensions.getColoredDrawableWithColor
import org.fossify.commons.extensions.getContrastColor
import org.fossify.commons.extensions.getProperTextColor
import org.fossify.commons.extensions.realScreenSize
import org.fossify.home.R
import org.fossify.home.activities.SimpleActivity
import org.fossify.home.databinding.ItemLauncherLabelBinding
import org.fossify.home.extensions.animateScale
import org.fossify.home.extensions.config
import org.fossify.home.helpers.AccessibilityFontHelper
import org.fossify.home.helpers.ColorBlindFilters
import org.fossify.home.helpers.ICON_LABEL_POSITION_HIDDEN
import org.fossify.home.helpers.ICON_LABEL_POSITION_RIGHT
import org.fossify.home.helpers.NotificationBadgeStore
import org.fossify.home.helpers.TEXT_SIZE_EXTRA_LARGE
import org.fossify.home.helpers.TEXT_SIZE_LARGE
import org.fossify.home.helpers.TEXT_SIZE_SMALL
import org.fossify.home.interfaces.AllAppsListener
import org.fossify.home.models.AppLauncher

class LaunchersAdapter(
    val activity: SimpleActivity,
    val allAppsListener: AllAppsListener,
    val itemClick: (Any) -> Unit
) : ListAdapter<AppLauncher, LaunchersAdapter.ViewHolder>(AppLauncherDiffCallback()),
    RecyclerViewFastScroller.OnPopupTextUpdate {

    private var textColor = activity.getProperTextColor()
    private var iconPadding = 0
    private var originalLabelTypeface: Typeface? = null

    init {
        setHasStableIds(true)
        calculateIconWidth()
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).getLauncherIdentifier().hashCode().toLong()
    }

    fun launchFirstApp(): Boolean {
        val launcher = currentList.firstOrNull() ?: return false
        itemClick(launcher)
        return true
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLauncherLabelBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        if (originalLabelTypeface == null) {
            // capture the style's typeface once, before any accessibility font override
            // ever touches it - every instance of this layout shares the same style
            originalLabelTypeface = binding.launcherLabel.typeface
        }
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(getItem(position))
    }

    override fun submitList(list: MutableList<AppLauncher>?) {
        calculateIconWidth()
        super.submitList(list)
    }

    private fun calculateIconWidth() {
        val currentColumnCount = activity.config.drawerColumnCount
        val iconWidth = activity.realScreenSize.x / currentColumnCount
        iconPadding = (iconWidth * 0.1f).toInt()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateTextColor(newTextColor: Int) {
        if (newTextColor != textColor) {
            textColor = newTextColor
            notifyDataSetChanged()
        }
    }

    // Text size levels scale the drawer label off its original XML size rather than its
    // current on-screen size, since views are recycled and would otherwise compound.
    private fun applyTextSize(binding: ItemLauncherLabelBinding) {
        val baseSizePx = binding.root.resources.getDimension(org.fossify.commons.R.dimen.smaller_text_size)
        val scale = when (activity.config.textSizeLevel) {
            TEXT_SIZE_SMALL -> 0.85f
            TEXT_SIZE_LARGE -> 1.15f
            TEXT_SIZE_EXTRA_LARGE -> 1.3f
            else -> 1f
        }
        binding.launcherLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, baseSizePx * scale)
    }

    // originalLabelTypeface is captured once in onCreateViewHolder, before any font
    // override is applied - reading it back off a recycled view would just return
    // whatever we set on its last bind.
    private fun applyAccessibilityFont(binding: ItemLauncherLabelBinding) {
        binding.launcherLabel.typeface = AccessibilityFontHelper.resolve(
            activity, activity.config.accessibilityFont, originalLabelTypeface
        )
    }

    // Same red circle + count as HomeScreenGrid's drawNotificationBadge(), just as a
    // real view here instead of a canvas draw since the drawer is a RecyclerView.
    private fun applyNotificationBadge(binding: ItemLauncherLabelBinding, launcher: AppLauncher) {
        val count = if (activity.config.showNotificationBadges) {
            NotificationBadgeStore.getCount(launcher.packageName)
        } else {
            0
        }

        if (count > 0) {
            binding.launcherBadge.text = if (count > 99) "99+" else count.toString()
            binding.launcherBadge.beVisible()
        } else {
            binding.launcherBadge.beGone()
        }
    }

    // Same shadow-boost approach as HomeScreenGrid's applyHighContrastSetting():
    // strengthens the existing look rather than swapping in a whole separate theme.
    private fun applyHighContrast(binding: ItemLauncherLabelBinding) {
        val shadowRadius = if (activity.config.highContrastMode) 5f else 0f
        binding.launcherLabel.setShadowLayer(shadowRadius, 0f, 0f, textColor.getContrastColor())
    }

    // Repositions the label relative to the icon. RIGHT places it beside the icon;
    // anything else (BOTTOM, or HIDDEN where the label is invisible anyway) uses the
    // original below-icon layout. Views are recycled, so rules from a previous bind
    // are cleared first rather than only ever added.
    private fun applyIconLabelPosition(binding: ItemLauncherLabelBinding, position: Int) {
        val iconParams = binding.launcherIcon.layoutParams as RelativeLayout.LayoutParams
        val labelParams = binding.launcherLabel.layoutParams as RelativeLayout.LayoutParams

        labelParams.removeRule(RelativeLayout.BELOW)
        labelParams.removeRule(RelativeLayout.END_OF)
        labelParams.removeRule(RelativeLayout.CENTER_VERTICAL)

        if (position == ICON_LABEL_POSITION_RIGHT) {
            iconParams.width = iconParams.height
            labelParams.width = RelativeLayout.LayoutParams.WRAP_CONTENT
            labelParams.addRule(RelativeLayout.END_OF, binding.launcherIcon.id)
            labelParams.addRule(RelativeLayout.CENTER_VERTICAL)
            labelParams.marginStart =
                binding.root.resources.getDimensionPixelSize(R.dimen.small_margin)
        } else {
            iconParams.width = RelativeLayout.LayoutParams.MATCH_PARENT
            labelParams.width = RelativeLayout.LayoutParams.MATCH_PARENT
            labelParams.addRule(RelativeLayout.BELOW, binding.launcherIcon.id)
            labelParams.marginStart = 0
        }

        binding.launcherIcon.layoutParams = iconParams
        binding.launcherLabel.layoutParams = labelParams
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        @SuppressLint("ClickableViewAccessibility")
        fun bindView(launcher: AppLauncher): View {
            val binding = ItemLauncherLabelBinding.bind(itemView)
            itemView.apply {
                binding.launcherLabel.text = launcher.title
                binding.launcherLabel.setTextColor(textColor)
                applyTextSize(binding)
                applyAccessibilityFont(binding)
                applyHighContrast(binding)
                applyNotificationBadge(binding, launcher)
                val labelPosition = activity.config.iconLabelPosition
                binding.launcherLabel.beVisibleIf(
                    activity.config.showDrawerAppLabels && labelPosition != ICON_LABEL_POSITION_HIDDEN
                )
                applyIconLabelPosition(binding, labelPosition)
                binding.launcherIcon.setPadding(iconPadding, iconPadding, iconPadding, 0)
                binding.launcherIcon.colorFilter = ColorBlindFilters.getColorFilter(activity.config.colorBlindMode)

                if (launcher.drawable != null && binding.launcherIcon.tag == true) {
                    binding.launcherIcon.setImageDrawable(launcher.drawable)
                } else {
                    val placeholderDrawable = activity.resources.getColoredDrawableWithColor(
                        drawableId = R.drawable.placeholder_drawable,
                        color = launcher.thumbnailColor
                    )
                    Glide.with(activity)
                        .load(launcher.drawable)
                        .placeholder(placeholderDrawable)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .into(object : DrawableImageViewTarget(binding.launcherIcon) {
                            override fun onResourceReady(
                                resource: Drawable,
                                transition: Transition<in Drawable>?
                            ) {
                                super.onResourceReady(resource, transition)
                                view.tag = true
                            }
                        })
                }

                setOnClickListener { itemClick(launcher) }
                setOnLongClickListener {
                    val location = IntArray(2)
                    getLocationOnScreen(location)
                    allAppsListener.onAppLauncherLongPressed(
                        x = (location[0] + width / 2).toFloat(),
                        y = location[1].toFloat(),
                        appLauncher = launcher
                    )
                    true
                }

                setOnTouchListener { _, event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            binding.launcherIcon.drawable.alpha = LAUNCHER_ALPHA_PRESSED
                            animateScale(
                                from = LAUNCHER_SCALE_NORMAL,
                                to = LAUNCHER_SCALE_PRESSED,
                                duration = LAUNCHER_SCALE_UP_DURATION
                            )
                        }

                        MotionEvent.ACTION_UP,
                        MotionEvent.ACTION_CANCEL -> {
                            binding.launcherIcon.drawable.alpha = LAUNCHER_ALPHA_NORMAL
                            animateScale(
                                from = LAUNCHER_SCALE_PRESSED,
                                to = LAUNCHER_SCALE_NORMAL,
                                duration = LAUNCHER_SCALE_DOWN_DURATION
                            )
                        }
                    }
                    false
                }
            }

            return itemView
        }
    }

    override fun onChange(position: Int) = currentList.getOrNull(position)?.getBubbleText() ?: ""

    companion object {
        private const val LAUNCHER_SCALE_NORMAL = 1f
        private const val LAUNCHER_SCALE_PRESSED = 1.15f
        private const val LAUNCHER_SCALE_UP_DURATION = 100L
        private const val LAUNCHER_SCALE_DOWN_DURATION = 50L
        private const val LAUNCHER_ALPHA_NORMAL = 255
        private const val LAUNCHER_ALPHA_PRESSED = 220
    }
}

private class AppLauncherDiffCallback : DiffUtil.ItemCallback<AppLauncher>() {
    override fun areItemsTheSame(oldItem: AppLauncher, newItem: AppLauncher): Boolean {
        return oldItem.getLauncherIdentifier().hashCode().toLong() ==
                newItem.getLauncherIdentifier().hashCode().toLong()
    }

    override fun areContentsTheSame(oldItem: AppLauncher, newItem: AppLauncher): Boolean {
        return oldItem.title == newItem.title &&
                oldItem.order == newItem.order &&
                oldItem.thumbnailColor == newItem.thumbnailColor &&
                oldItem.drawable != null &&
                newItem.drawable != null
    }
}
