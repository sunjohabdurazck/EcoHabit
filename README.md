🌱 EcoHabit: Track Your Sustainable Journey
https://img.shields.io/badge/Java-17-orange.svg
https://img.shields.io/badge/JavaFX-19-blue.svg
https://img.shields.io/badge/SQLite-3.x-lightgrey.svg
https://img.shields.io/badge/License-MIT-yellow.svg

A desktop application that empowers individuals to track, visualize, and understand their personal environmental impact through gamification and data-driven insights.

Visualize your sustainability journey with interactive dashboards

✨ Features
🎯 Core Functionality
🌍 Carbon Footprint Tracking: Log daily activities and see real-time CO₂ savings calculations

📊 Interactive Analytics: Visualize your impact with line, bar, pie, and area charts

🏆 Gamification System: Earn badges and maintain streaks for sustainable habits

🤖 EcoBuddy Assistant: Rules-based chatbot for sustainability guidance

💡 Educational Eco-Tips: Personalized daily tips for sustainable living

🎨 User Experience
Multiple Themes: Light, Dark, Eco, and System theme support

Responsive Design: Clean, intuitive interface built with JavaFX

Real-time Updates: Live dashboard updates with ObservableList patterns

Offline-First: Fully functional without internet connection

🛠️ Technology Stack
Component	Technology
Language	Java 17
UI Framework	JavaFX (FXML + CSS)
Database	SQLite with JDBC
Architecture	MVC Pattern with DAO Layer
Build Tool	Gradle
Version Control	GitHub
📥 Installation
Prerequisites
Java Development Kit (JDK) 17 or higher

JavaFX SDK 19 or higher

Gradle Build Tool

Quick Start
Clone the repository

bash
git clone https://github.com/sunjohabdurazck/EcoHabit.git
cd EcoHabit
Build the project

bash
gradle build
Run the application

bash
gradle run
For Developers
Import project into Eclipse or IntelliJ IDEA

Ensure JavaFX SDK is properly configured in your IDE

The database will be automatically created on first run

🗄️ Database Schema
EcoHabit uses a relational SQLite database with the following main tables:

users: User profiles and preferences

activities: Logged sustainable activities with CO₂ calculations

badges: Gamification and achievement system

tips: Educational eco-tips and recommendations

user_badges: Junction table for user achievements

For detailed schema information, see Database Schema Details.

🔧 Key Features Deep Dive
CO₂ Calculation Engine
java
// Example conversion factors
BIKING_VS_DRIVING: 0.21 kg CO₂ per km
PUBLIC_TRANSPORT: 0.14 kg CO₂ per km  
ENERGY_SAVED: 0.5 kg CO₂ per kWh
PLANT_BASED_MEAL: 2.5 kg CO₂ per meal
Gamification System
Badge Categories: Carbon Saver, Streak Master, Event Participant

Progress Tracking: Real-time milestone monitoring

Celebration Overlays: Visual feedback for achievements

Session Management
Singleton-based SessionManager ensures consistent user state across all application modules.

👥 Team Contributions
Team Member	Focus Areas
Sunjoh Abdurazack	Database Design, Backend Systems, Activity Management
Usman Jabir	Analytics & Visualization, Gamification System
Amadu Gbanyawai	UI/UX Design, Authentication, Educational Content
🚧 Challenges & Solutions
Challenge	Solution
UI Freezing	Implemented JavaFX asynchronous tasks for heavy operations
Database Locking	Used transaction-based queries with commit/rollback
State Management	Singleton SessionManager for consistent user state
Feature Integration	Agile sprints with regular GitHub code reviews
📊 Project Impact
Alignment with UN Sustainable Development Goals
✅ Goal 13: Climate Action

✅ Goal 11: Sustainable Cities and Communities

✅ Goal 12: Responsible Consumption and Production

Testing Results
Unit Tests: 95.7% success rate (45/47 tests passed)

Integration Tests: 93.3% success rate (28/30 tests passed)

UI Tests: 95.0% success rate (38/40 tests passed)

User Acceptance: 100% success rate (15/15 tests passed)

🚀 Future Roadmap
Phase 1 (Q1 2026)
LLM-powered chatbot integration

Enhanced 3D data visualization

Advanced filtering capabilities

Phase 2 (Q2 2026)
Cloud synchronization

Multi-device support

RESTful API development

Phase 3 (Q3 2026)
Mobile companion application

Social features and community challenges

Smart home device integration

🤝 Contributing
We welcome contributions! Please see our Contributing Guidelines for details.

Fork the repository

Create a feature branch (git checkout -b feature/amazing-feature)

Commit your changes (git commit -m 'Add some amazing feature')

Push to the branch (git push origin feature/amazing-feature)

Open a Pull Request

📄 License
This project is licensed under the MIT License - see the LICENSE file for details.

🙏 Acknowledgments
Ishmam Tashdeed - Project Supervisor

Islamic University of Technology - Department of CSE

Peer Testers - Valuable feedback and testing

Open Source Community - Tools and libraries that made this project possible

📞 Support & Contact
For bugs, feature requests, or help:

📧 Email: sunjoh@iut-dhaka.edu

🐛 GitHub Issues: Create an issue

<div align="center">
Make every habit count for our planet 🌍

Empowering individuals through data-driven sustainability tracking

</div>
