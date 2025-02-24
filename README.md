# ComposeDemo
![ComposeDemoLogo](https://github.com/user-attachments/assets/dcb58d38-dbc1-4feb-866d-09b6bc5eea8c)

![image](https://github.com/user-attachments/assets/54955699-7fb0-48bf-b3f2-f05d90a53d3c)

## Overview
**ComposeDemo** is an Android application created as part of a Mobile Team sharing session on **Jetpack Compose**. This app demonstrates various aspects of Jetpack Compose, providing a comparison with the traditional View-based UI and covering fundamental to advanced topics.

## Features
The app includes multiple screens showcasing different Jetpack Compose concepts:

1. **Why Compose?**  
   - Compare Jetpack Compose and the traditional View-based approach.
   
2. **Simple Compose Components**  
   - Basic UI components such as Text, Button, Image, Row, Column, etc.
   
3. **Compose Modifiers**  
   - Demonstrating different modifiers like padding, background, click handling, and more.
   
4. **State Management**  
   - Handling state using `remember`, `mutableStateOf`, and `State` APIs.
   
5. **Advanced State Management (ViewModel)**  
   - Managing state with `ViewModel` and `StateFlow` in Compose.
   
6. **Side Effects**  
   - Using `LaunchedEffect`, `DisposableEffect`, and `SideEffect` for lifecycle-aware operations.
   
7. **Theming**  
   - Applying `ColorScheme`, `Typography`, and creating a custom theme.
   
8. **Interoperability**  
   - Embedding Views inside Compose (`AndroidView`).
   - Using Compose inside View (`ComposeView`).

## Architecture
- **Single Activity Pattern**: The app follows the **Single Activity Architecture**, where all screens are managed using **Jetpack Compose Navigation**.
- **Jetpack Compose Navigation**: Used for navigating between different screens efficiently.
- **MVVM Architecture**: ViewModels are utilized to manage UI state and business logic.
- **Compose** UI Components: Located in edts.android.composedemo.ui package.
- **View-based** UI Components: Located in edts.android.composedemo.ui_view package.

## Tech Stack
- **Jetpack Compose** (UI)
- **Compose Navigation** (Screen transitions)
- **ViewModel & LiveData** (State management)
- **Material3** (Modern UI components & theming)

### Installation
1. Clone the repository:
   ```sh
   git clone https://github.com/Dhanfinix/Compose_Demo.git
   ```
2. Open the project in **Android Studio**.
3. Sync Gradle and build the project.
4. Run the app on an emulator or a physical device.


