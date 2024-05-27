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

#### Why choose MVP Architecture
MVP is suitable for small and common application, not complicated as MVVM or other architectures
Implementation of MVP is not time consuming and robust source code

## User Stories
1. **Current Location**: As a user, I can see my current location on a map.
2. **Save Note**: As a user, I can save a short note at my current location.
3. **View My Notes**: As a user, I can see notes that I have saved at the location they were saved on the map.
4. **View Others' Notes**: As a user, I can see the location, text, and user-name of notes other users have saved.
5. **Search Notes**: As a user, I have the ability to search for a note based on contained text or user-name.

## Deliverables
1. **Source Code**
    - Entire source code, including resources.
    - Code is well commented and original.
    - Code is compatible with Android Studio.
2. **Readme Overview**
    - Outline of the approach.
    - Time allocation for different tasks.
    - Known issues and limitations.

## Time Management
- **Planning**: 2 hours
- **Development**: 10 hours
- **Documentation**: 1 hour

## Known Issues and Limitations
- **Performance**: Optimization needed for large datasets.
- **UI**: Needs enhancement for better user experience.
- **Features**: Some advanced features are not implemented due to time constraints.

## Step-by-Step Development for Each User Story

### 1. Current Location
**Goal**: Display the user's current location on a map.
1. **Integrate Map SDK**: Integrate Google Maps SDK into the project.
2. **Request Permissions**: Implement runtime permissions for accessing the device's location.
3. **Get Current Location**: Use the FusedLocationProviderClient to get the current location of the user.
4. **Display Location**: Display the current location on the map with a marker.

### 2. Save Note
**Goal**: Allow users to save a short note at their current location.
1. **UI for Adding Notes**: Create a UI component (e.g., a dialog or a bottom sheet) for entering notes.
2. **Capture Note Details**: Capture the note text and the current location coordinates when the user saves a note.
3. **Store Notes**: Save the note details in Firebase Firestore with the owner note name (email use for authentication), note text, and location coordinates.
4. **Confirmation**: Provide feedback to the user upon successful saving of the note.

### 3. View My Notes
**Goal**: Display notes that the user has saved on the map.
1. **Retrieve Notes**: Fetch notes from Firebase Firestore that belong to the current user.
2. **Map Markers**: Place custom markers (including owner name, note content) on the map at the saved locations of the notes.

### 4. View Others' Notes
**Goal**: Display notes saved by other users.
1. **Retrieve All Notes**: Fetch all notes from Firebase Firestore.
2**Map Markers**: Place custom markers on the map for notes from other users.

### 5. Search Notes
**Goal**: Allow users to search for notes based on text or user-name.
1. **Search UI**: Implement a search bar or dialog for entering search queries.
2. **Filter Notes**: Query Firebase Firestore to find notes matching the search text or user-name.
3. **Display Results**:  Display the matching notes on the list under search box
4. **Clear Search**: Provide an option to clear the search and display all notes again.