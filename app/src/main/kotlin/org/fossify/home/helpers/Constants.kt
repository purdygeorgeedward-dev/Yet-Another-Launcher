package org.fossify.home.helpers

const val WIDGET_LIST_SECTION = 0
const val WIDGET_LIST_ITEMS_HOLDER = 1

const val REPOSITORY_NAME = "Launcher"

// shared prefs
const val WAS_HOME_SCREEN_INIT = "was_home_screen_init"
const val HOME_ROW_COUNT = "home_row_count"
const val HOME_COLUMN_COUNT = "home_column_count"
const val DRAWER_COLUMN_COUNT = "drawer_column_count"
const val SHOW_SEARCH_BAR = "show_search_bar"
const val CLOSE_APP_DRAWER = "close_app_drawer"
const val AUTO_SHOW_KEYBOARD_IN_APP_DRAWER = "auto_show_keyboard_in_app_drawer"
const val SHOW_DRAWER_APP_LABELS = "show_drawer_app_labels"
const val SHOW_HOME_APP_LABELS = "show_home_app_labels"

// accessibility / Tier 1 preferences
// Pref keys backing Config.kt properties of the same purpose - see there for behavior.
const val HAPTIC_FEEDBACK_INTENSITY = "haptic_feedback_intensity"
const val HIGH_CONTRAST_MODE = "high_contrast_mode"
const val ACCESSIBILITY_FONT = "accessibility_font"
const val TEXT_SIZE_LEVEL = "text_size_level"
const val DARK_MODE_SCHEDULING = "dark_mode_scheduling"
const val DARK_MODE_START_HOUR = "dark_mode_start_hour"
const val DARK_MODE_START_MINUTE = "dark_mode_start_minute"
const val DARK_MODE_END_HOUR = "dark_mode_end_hour"
const val DARK_MODE_END_MINUTE = "dark_mode_end_minute"
const val ONE_HANDED_MODE = "one_handed_mode"
const val GAMING_MODE = "gaming_mode"
const val MEETING_MODE = "meeting_mode"
const val COLOR_BLIND_MODE = "color_blind_mode"
const val ICON_LABEL_POSITION = "icon_label_position"
const val GRADIENT_FOLDER_BACKGROUND = "gradient_folder_background"
const val GLASSMORPHISM_UI = "glassmorphism_ui"

// text size levels, ordered smallest to largest
const val TEXT_SIZE_SMALL = 0
const val TEXT_SIZE_NORMAL = 1
const val TEXT_SIZE_LARGE = 2
const val TEXT_SIZE_EXTRA_LARGE = 3

// accessibility font choice - actual bundled TrueType/OpenType fonts under res/font,
// both OFL-licensed (license text bundled in assets/font_licenses)
const val FONT_DEFAULT = 0
const val FONT_LEXEND = 1
const val FONT_OPEN_DYSLEXIC = 2

// color blind mode types - determines which color filter matrix gets applied
const val COLOR_BLIND_NONE = 0
const val COLOR_BLIND_PROTANOPIA = 1
const val COLOR_BLIND_DEUTERANOPIA = 2
const val COLOR_BLIND_TRITANOPIA = 3

// where the app label renders relative to its icon
const val ICON_LABEL_POSITION_BOTTOM = 0
const val ICON_LABEL_POSITION_RIGHT = 1
const val ICON_LABEL_POSITION_HIDDEN = 2

const val DEFAULT_HAPTIC_INTENSITY = 50

// default home screen grid size
const val ROW_COUNT = 6
const val COLUMN_COUNT = 5
const val MIN_ROW_COUNT = 2
const val MAX_ROW_COUNT = 15
const val MIN_COLUMN_COUNT = 2
const val MAX_COLUMN_COUNT = 15

const val UNINSTALL_APP_REQUEST_CODE = 50
const val REQUEST_CONFIGURE_WIDGET = 51
const val REQUEST_ALLOW_BINDING_WIDGET = 52
const val REQUEST_CREATE_SHORTCUT = 53
const val REQUEST_SET_DEFAULT = 54
const val REQUEST_EXPORT_LAYOUT = 55
const val REQUEST_IMPORT_LAYOUT = 56

const val ITEM_TYPE_ICON = 0
const val ITEM_TYPE_WIDGET = 1
const val ITEM_TYPE_SHORTCUT = 2
const val ITEM_TYPE_FOLDER = 3

const val WIDGET_HOST_ID = 12345
const val MAX_CLICK_DURATION = 150
