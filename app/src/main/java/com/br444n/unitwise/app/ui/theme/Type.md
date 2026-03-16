# Typography Rules — UnitWise

## Font Family

The application uses **Plus Jakarta Sans** as the only font family.

The goal is clarity, readability, and a clean supermarket-style utility interface.

This is a small utility app, therefore typography must remain simple and consistent.

---

## Installed Font Weights

Only the following weights are allowed:

| Weight         | Usage                        |
| -------------- | ---------------------------- |
| Regular (400)  | Body text & user input       |
| Medium (500)   | Labels & subtitles           |
| SemiBold (600) | Titles & App branding        |
| Bold (700)     | Important results & emphasis |

Do NOT add additional weights unless strictly necessary.

---

## Typography Hierarchy

### 1. App Identity

Used for:

* App name
* TopAppBar title

Weight:
SemiBold (600)

---

### 2. Section Titles

Used for:

* Screen titles
* Important headers

Weight:
Medium (500)

---

### 3. Labels

Used for:

* TextField labels
* Dropdown labels

Weight:
Medium (500)

---

### 4. Body Text

Used for:

* User-entered content
* Descriptions
* Informational text

Weight:
Regular (400)

---

### 5. Result Highlight

Used for:

* Comparison results
* Savings messages

Weight:
Bold (700)

---

## Compose Usage Guidelines

Typography must be accessed through MaterialTheme:

Example:

Text(
text = "Product",
style = MaterialTheme.typography.titleMedium
)

Do NOT create custom TextStyles inside composables.

---

## Design Principles

* Typography must never compete with functionality.
* Information clarity is prioritized over visual decoration.
* Avoid excessive font scaling.
* Maintain consistent hierarchy across screens.

---

## Forbidden Practices

❌ Mixing multiple font families
❌ Using italic variants
❌ Hardcoding font weights in UI
❌ Creating random TextStyles per screen

All typography decisions belong inside the theme layer.
