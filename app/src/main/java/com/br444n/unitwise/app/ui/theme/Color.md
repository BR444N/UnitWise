# Color System — UnitWise

## Design Philosophy

UnitWise uses a minimal color palette focused on clarity, trust, and readability.

Colors must support fast decision-making rather than visual decoration.

---

## Color Palette

| Role                  | Hex           | Usage                 |
|-----------------------|---------------|-----------------------|
| Primary               | #0DF246       | Main accent color     |
| Primary Text/Icon     | #102214       | App name & main icons |
| Subtitle Text         | #64748B       | Secondary information |
| App Background        | #F5F8F6       | Screen background     |
| Card Background       | #FFFFFF       | Cards & surfaces      |
| TextField Border      | #6B7280       | Inputs outline        |
| TextField Text        | #6B7280       | User input text       |
| Disabled / Unfocused  | #0DF246 (40%) | Unfocused states      |
| Badges                | #334155       | Just Badges           |

---

## Color Roles Explained

### Primary (#0DF246)

Used for:

* Buttons
* Active states
* Highlights
* Focus indicators

Never use for long text blocks.

---

### Primary Text (#102214)

Used for:

* App title
* Important icons
* High emphasis text

Provides strong contrast against light background.

---

### Subtitle Text (#64748B)

Used for:

* Labels
* Secondary descriptions
* Supporting information

Must never overpower primary content.

---

### Background (#F5F8F6)

Default screen background.

All screens must use this color unless a surface is required.

---

### Card Surface (#FFFFFF)

Used for:

* Cards
* Result containers
* Elevated UI elements

---

### TextFields (#6B7280)

Used for:

* Input borders
* Placeholder text
* Input values

Ensures neutral interaction visuals.

---

### Unfocused State (Primary 40%)

Primary color with 40% opacity.

Used for:

* Disabled buttons
* Unfocused inputs
* Passive indicators

---

## Compose Implementation Rules

All colors must be accessed through the theme:

MaterialTheme.colorScheme.primary

Do NOT hardcode hex values inside composables.

---

## Accessibility Rules

* Maintain readable contrast.
* Avoid using primary green for body text.
* Background + text combinations must remain legible in light and dark theme.

---

## Forbidden Practices

❌ Hardcoded Color(0xFF...) in UI
❌ Random color additions
❌ Multiple greens outside palette
❌ Gradient usage (not part of design system)
✅ Badge color: #334155 (used exclusively for badges and status indicators)

All colors must originate from the theme system.
