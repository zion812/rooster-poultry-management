# Rooster Poultry Management – Localization & Community

---

## 1. Localization Process
- **Supported Languages:** English, Telugu (add more via `res/values-xx/` in Android app)
- **Android:**
  - All user-facing strings in `res/values/strings.xml`
  - Add translations in `res/values-te/strings.xml` (Telugu) and other locales as needed
  - Use `tools:locale` for previewing translations in Android Studio
- **Backend/Cloud:**
  - Ensure all user-facing messages support i18n (if applicable)
  - Use translation files or libraries (e.g., i18next for Node.js)

---

## 2. Accessibility
- Use content descriptions for all interactive UI elements
- Ensure color contrast and font scaling are accessible
- Test with screen readers and accessibility tools

---

## 3. Community & Contribution
- See `CONTRIBUTING.md` for guidelines
- Use GitHub Discussions and Issues for support and feature requests
- Add yourself to the CONTRIBUTORS section in `README.md` (if available)

---

## 4. Next Steps
- Expand translation coverage for all new features
- Periodically review accessibility compliance
- Encourage community contributions for new locales and accessibility improvements
