
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


#### Why Choose MVP Architecture
MVP is ideal for small and standard applications, less complex than MVVM or other architectures. Implementing MVP is time-efficient and results in robust source code.

## User Stories
1. **Current Location**: As a user, I can see my current location on a map.
2. **Save Note**: As a user, I can save a short note at my current location.
3. **View My Notes**: As a user, I can see notes that I have saved at the location they were saved on the map.
4. **View Others' Notes**: As a user, I can see the location, text, and user-name of notes other users have saved.
5. **Search Notes**: As a user, I have the ability to search for a note based on contained text or user-name.

##  Scenario
Typically, I work eight hours a day from Monday to Friday and have weekends off. However, during the week of this test, my time is constrained for various reasons. I have a family event on Friday and Saturday. Moreover, I have an English test scheduled for Wednesday, May 29th. To prepare for this test, I need to manage my time efficiently, as I have at most four hours per day after considering sleep and other daily activities. Finally, I have 20 hours to complete this test.

## Time Management
- **Revise knowledge for Kotlin**: 4 hours
- **Planning**: 2 hours
- **Development**: 12 hours
- **Documentation**: 1 hour

## Known Issues and Limitations
- **Performance**: Optimization needed for larger data.
- **UI**: Needs enhancement for better user experience.
- **Features**: Some advanced features are not implemented due to time constraints, finished 4/5 User Story.

## Step-by-Step Development for Each User Story

### 1. Current Location
**Goal**: Display the user's current location on a map.
1. **Integrate Map SDK**: Integrate Google Maps SDK into the project.
2. **Request Permissions**: Implement runtime permissions for accessing the device's location.
3. **Get Current Location**: Use the FusedLocationProviderClient to get the current location of the user.
4. **Display Location**: Display the current location on the map with a marker.

### 2. Save Note
**Goal**: Allow users to save a short note at their current location.
1. **UI for opening note add dialog**: Create a button to open a box for adding note.
2. **UI for Adding Notes**: Create a UI component (e.g., a dialog or a bottom sheet) for entering notes.
3. **Capture Note Details**: Capture the note text and the current location coordinates when the user saves a note.
4. **Store Notes**: Save the note details in Firebase Firestore with the owner note name (email use for authentication), note text, and location coordinates.
5. **Confirmation**: Provide feedback to the user upon successful saving of the note.

### 3. View My Notes
**Goal**: Display notes that the user has saved on the map.
1. **Retrieve Notes**: Fetch notes from Firebase Firestore that belong to the current user.
2. **Map Markers**: Place custom markers (including owner name, note content) on the map at the saved locations of the notes.

### 4. View Others' Notes
**Goal**: Display notes saved by other users.
1. **Retrieve All Notes**: Fetch all notes from Firebase Firestore.  
   2**Map Markers**: Place custom markers on the map for notes from other users.

### 5. Search Notes  (Not finished implementation yet, stucked at Step 2)
**Goal**: Allow users to search for notes based on text or user-name.
1. **Search UI**: Implement a search bar or dialog for entering search queries.
2. **Filter Notes**: Query Firebase Firestore to find notes matching the search text or user-name.
3. **Display Results**:  Display the matching notes on the list under search box.

**Experiments done on stucked step**
I have tried using two methods to query data on Firestore, including *whereEqualTo* and *whereArrayContains*, but I only get notes that fully match the query string. For example, when querying to find notes with the word "Hello," it only returns notes where "Hello" is the entire content of the note, but not "Hello Vietnam," where "Hello" is just a word in the note.
```kotlin
// Function to query documents by a specific key and value  fun 
queryDocumentsByKey(collectionName: String, key: String, value: Any, callback: (List<Map<String, Any>>) -> Unit) { 
	val db: FirebaseFirestore = Firebase.firestore 
	db.collection(collectionName).whereEqualTo(key, value).get().addOnSuccessListener 		{
		documents -> val results = mutableListOf<Map<String, Any>>() 
		for (document in documents) { 
			results.add(document.data) 
		} 
		callback(results) 
	}
	.addOnFailureListener { 
		exception -> println("Error querying documents: $exception") 
		callback(emptyList()) 
	} 
}
```
```kotlin
db.collection(collectionName).whereArrayContains("searchTerms", searchTerm).get().addOnSuccessListener { 
	documents -> val results = mutableListOf<Map<String, Any>>() 
	for (document in documents) { 
		results.add(document.data) 
	} 
	callback(results) 
} 
.addOnFailureListener { 
	exception -> println("Error querying documents: $exception") 
	callback(emptyList()) 
}
```

## Implicit requirements
Since the app requires access to personal and other notes, it needs to have an authentication function to separate users and identify which note belongs to whom. Additionally, the development process can easily create a new user for testing user story requirements. For this feature, I have utilized Firebase Authentication Service, which includes Register, Login, and Sign out functionalities.