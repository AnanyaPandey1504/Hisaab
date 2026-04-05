# Hisaab 💰 - Personal Finance Companion

Hisaab is a lightweight, offline-first personal finance companion app that helps users understand their daily money habits. It goes beyond a simple ledger by focusing on active engagement—merging highly visual dashboards, categorized insight tracking, and habit-forming gamification elements (like a dynamic "No-Spend Streak" engine) into a beautifully unified experience.

---

## 🎯 Project Overview & Core Features

### 1. Home Dashboard
The dashboard delivers an instant financial pulse at a glance:
- **Hero Metrics**: Always-visible Current Balance, Total Income, and Total Expenses.
- **Categorical Spending**: Interactive Pie Chart visualizing expense distributions.
- **Weekly Trend**: Bar Chart plotting the last 7 days of spending to identify high-outflow days instantly.

### 2. Transaction Management
A fast, swipe-friendly ledger built to make logging effortless:
- **Instant Search & Filters**: Live, instantaneous sorting by Income/Expense and keyword exactly as you type.
- **Gestures**: Standardized intuitive "Swipe-to-delete" for quick management.
- **Intelligent Forms**: Automatic dropdown adaptions (showing Salary/Freelance for Income, Food/Transit for Expense) preventing invalid category creation.

### 3. Gamification: Savings Goals & "No-Spend" Streaks
To elevate the product beyond a standard tracker, Hisaab actively encourages positive habits:
- **Savings Goals Tracker**: Users define targets and deadlines with visual completion metrics.
- ✨ **Dynamic No-Spend Streak Engine**: A custom tracker that calculates consecutive "Zero Expense" days. This gamifies saving by holding users accountable to their current and all-time saving records.

### 4. Interactive Insights
A dedicated analytics screen that respects limited mobile real estate:
- **Toggle Driven Analytics**: Swapping between Income and Expense smoothly transforms the pie distributions in real-time.
- **Ranked Lists**: Displays sorted transaction frequency with percentage scaling dynamically computed against global totals.

### 5. Highly Polished UX 
- **Material 3 Design System**: Mapped a completely custom "Emerald" brand palette targeting native components.
- **Global Dark Mode**: Implemented a fun Sun/Moon toggle to dynamically shift OS-level themes instantly. All charts and text automatically adapt for perfect contrast.
- **Edge-to-Edge UIs**: Modern Window Insets pad the Bottom Navigation so system gesture bars overlap the nav-bar seamlessly.
- **Empty States**: Friendly placeholders dynamically render when lists or charts are empty.

---

## 🤔 Assumptions & Product Decisions

While building Hisaab, product-focused assumptions were made to optimize the user flow:

1. **Local-First Architecture (Privacy)**
   *Assumption:* Users prefer their daily financial habits to remain strictly private and accessible offline.
   *Decision:* Rather than building a simulated network layer, a robust local persistence architecture was implemented. This allows for lightning-fast load times and represents how modern secure offline-first apps operate.

2. **The "No-Spend Streak" Definition**
   *Assumption:* A habit tracker needs definitive bounds to compute accuracy cleanly.
   *Decision:* The streak explicitly tracks whole days where zero transactions flagged as `EXPENSE` exist. Income entries do not break the streak.

3. **Combined Add/Edit Interface**
   *Decision:* To keep the user experience uniform, creating a new transaction and editing an existing one utilize the exact same form interface, dynamically adapting its Save/Update functionality behind the scenes.

---

## 🚀 Setup & Build Instructions

1. **Clone the repository** to your local machine.
2. Open the project in **Android Studio** (Koala or newer recommended).
3. Wait for standard Gradle synchronization. 
4. **Build & Run**:
   - Click **Run (Shift+F10)** to deploy to an Emulator (API 26+) or a physical device. 
   - Note: No internet connection or API keys are required to use the app entirely.
5. *Validation:* The app compiles with 0 errors or warnings when running standard assembly tasks.
