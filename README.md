# Landmark Remark Document

## Project Overview
**Landmark Remark** is a mobile application that allows users to save location-based notes on a map. Users can see their current location, save notes at their location, view notes saved by themselves and others, and search for notes based on text or user-name.

## Technology Stack
- **Language**: Kotlin
- **Target API Level**: 26 (minimum)
- **IDE**: Android Studio
- **Backend-as-a-Service**: Firebase

## Architecture
The application follows the **Model-View-Presenter (MVP)** architecture:

### Model
Handles data management, including storing and retrieving notes.

### View
Manages the display and user interaction, showing the map and notes.

### Presenter
Acts as an intermediary between Model and View, applying logic and updating the View.

## User Stories
1. **Current Location**: As a user, I can see my current location on a map.
2. **Save Note**: As a user, I can save a short note at my current location.
3. **View My Notes**: As a user, I can see notes that I have saved at the location they were saved on the map.
4. **View Others' Notes**: As a user, I can see the location, text, and user-name of notes other users have saved.
5. **Search Notes**: As a user, I have the ability to search for a note based on contained text or user-name.

## Development Approach
- **Backend Support**: Firebase is used for backend services.
