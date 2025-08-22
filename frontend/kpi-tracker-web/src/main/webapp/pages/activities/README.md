# Activities Management - Separation of Concerns

## Overview
The activities management system has been restructured to provide clear separation of concerns and better organization of different activity types.

## File Structure

### 1. **OrganizationalActivitiesView.xhtml** - NEW
- **Purpose**: Manages organization-wide activities
- **Scope**: Activities linked to organization goals
- **Features**: 
  - Organization-specific header with globe icon
  - Statistics cards for organization activities
  - Search and filtering capabilities
  - Clear filters button
  - Table showing organization goal information
  - **Form**: Uses `OrganizationalActivityFormDialog.xhtml` (organization goals only)

### 2. **OrganizationalActivityFormDialog.xhtml** - NEW
- **Purpose**: Dedicated form for creating/editing organizational activities
- **Features**:
  - Only shows organization goal selection (required)
  - Activity type and priority fields
  - All standard activity fields (title, description, dates, user, status)
  - Clean, focused interface for organizational activities

### 3. **DepartmentActivitiesView.xhtml** - ENHANCED
- **Purpose**: Manages department-level activities
- **Scope**: Activities linked to department goals within the user's department
- **Features**:
  - Department-specific header with building icon
  - Current department display
  - Statistics cards for department activities
  - Search and filtering capabilities
  - Clear filters button
  - Contextual filtering by department
  - **Form**: Uses `DepartmentActivityFormDialog.xhtml` (department goals only)

### 4. **DepartmentActivityFormDialog.xhtml** - NEW
- **Purpose**: Dedicated form for creating/editing department activities
- **Features**:
  - Only shows department goal selection (required)
  - Activity type and priority fields
  - All standard activity fields (title, description, dates, user, status)
  - Clean, focused interface for department activities

### 5. **TeamActivitiesView.xhtml** - ENHANCED
- **Purpose**: Manages team-level activities
- **Scope**: Activities linked to team goals within the user's team
- **Features**:
  - Team-specific header with users icon
  - Current team display
  - Statistics cards for team activities
  - Search and filtering capabilities
  - Clear filters button
  - Contextual filtering by team
  - **Form**: Uses `TeamActivityFormDialog.xhtml` (team goals only)

### 6. **TeamActivityFormDialog.xhtml** - NEW
- **Purpose**: Dedicated form for creating/editing team activities
- **Features**:
  - Only shows team goal selection (required)
  - Activity type and priority fields
  - All standard activity fields (title, description, dates, user, status)
  - Clean, focused interface for team activities

### 7. **ActivitiesView.xhtml** - RESTRUCTURED
- **Purpose**: Main hub for activity management and overview
- **Scope**: All system activities with navigation to specific types
- **Features**:
  - Activity type navigation cards
  - System-wide statistics overview
  - Navigation to specific activity types
  - Comprehensive activity table with goal type information
  - Clear filters button
  - **Form**: Uses `ActivityFormDialog.xhtml` (generic form for all goal types)

### 8. **ActivityFormDialog.xhtml** - ENHANCED
- **Purpose**: Generic form for system-wide activities
- **Features**:
  - Shows all goal types (organization, department, team)
  - Goal selection logic to ensure only one goal type is selected
  - All standard activity fields
  - Used for the main system activities view

## Separation of Concerns

### **Organizational Level**
- Activities linked to organization goals
- Visible to all users across the organization
- Strategic, high-level activities
- Green color scheme and globe icon

### **Department Level**
- Activities linked to department goals
- Scoped to user's department
- Tactical, department-specific activities
- Blue color scheme and building icon

### **Team Level**
- Activities linked to team goals
- Scoped to user's team
- Operational, team-specific activities
- Purple color scheme and users icon

### **System Level**
- Overview of all activities
- Navigation hub to specific types
- Cross-cutting statistics and management
- Orange color scheme and list icon

## Navigation Flow

```
ActivitiesView (Main Hub)
├── Organizational Activities → OrganizationalActivitiesView
├── Department Activities → DepartmentActivitiesView
├── Team Activities → TeamActivitiesView
└── All Activities → ActivitiesView (comprehensive table)
```

## Key Benefits

1. **Clear Separation**: Each view focuses on a specific activity scope
2. **Contextual Information**: Users see relevant context (current department/team)
3. **Improved Navigation**: Easy switching between activity types
4. **Better Organization**: Logical grouping of related activities
5. **Enhanced UX**: Visual indicators and consistent styling
6. **Scalability**: Easy to add new activity types in the future

## Implementation Details

- **Specific Forms**: Each activity type now has its own dedicated form:
  - `OrganizationalActivityFormDialog.xhtml` - Only shows organization goals
  - `DepartmentActivityFormDialog.xhtml` - Only shows department goals  
  - `TeamActivityFormDialog.xhtml` - Only shows team goals
  - `ActivityFormDialog.xhtml` - Generic form for system-wide activities
- Each view has its own backing bean for proper data isolation
- Statistics are calculated per view scope
- Filtering and search work within the respective scope
- Clear filters functionality added to all views
- Consistent styling and layout across all views
- **Enhanced User Experience**: Users see only relevant goal options for each activity type

## Future Enhancements

- Activity type indicators in the main table
- Cross-scope activity reporting
- Activity templates for different scopes
- Bulk operations per scope
- Activity dependency tracking across scopes
