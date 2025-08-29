# KPI Dynamic Data Implementation Guide

## Overview
This document outlines how to implement actual dynamic data for KPI tracking instead of the mock data currently used in the system.

## Data Sources and Model Structure

### 1. KPI Model (`KPI.java`)
**Location**: `/backend/kpi-tracker-models/src/main/java/org/pahappa/systems/kpiTracker/models/kpis/KPI.java`

**Key Fields for Update Tracking**:
- `lastUpdated` - Timestamp of last KPI modification
- `currentValue` - Current KPI value 
- `accomplishmentPercentage` - Calculated achievement percentage
- **BaseEntity Audit Fields** (automatically populated):
  - `dateCreated` - When the KPI was first created
  - `createdBy` - User who created the KPI
  - `dateChanged` - Last modification timestamp
  - `changedBy` - User who last modified the KPI

### 2. New KPI Update History Model (`KpiUpdateHistory.java`)
**Location**: `/backend/kpi-tracker-models/src/main/java/org/pahappa/systems/kpiTracker/models/kpis/KpiUpdateHistory.java`

**Purpose**: Dedicated entity to track detailed KPI value changes over time.

**Fields**:
- `kpi` - Reference to the KPI being updated
- `previousValue` - Value before the update
- `newValue` - Value after the update
- `updateComment` - Optional comment explaining the update
- `accomplishmentPercentage` - Achievement percentage at time of update
- `updateDate` - When the update occurred
- `updatedByUser` - User who made the update
- `updatedByStaff` - Staff record of the user (if available)

### 3. Services Layer

#### KpiUpdateHistoryService
**Interface**: `/backend/kpi-tracker-services/src/main/java/org/pahappa/systems/kpiTracker/core/services/kpis/KpiUpdateHistoryService.java`
**Implementation**: `/backend/kpi-tracker-services/src/main/java/org/pahappa/systems/kpiTracker/core/services/kpis/impl/KpiUpdateHistoryServiceImpl.java`

**Key Methods**:
- `getUpdateHistoryByKpi(KPI kpi)` - Get all updates for a KPI
- `createUpdateHistory(...)` - Create new update record
- `getLatestUpdateByKpi(KPI kpi)` - Get most recent update
- `getUpdateHistoryByKpiAndDateRange(...)` - Get updates within date range

## Implementation Strategy

### Option 1: Using BaseEntity Audit Fields (Recommended for Basic Tracking)

**Advantages**:
- ✅ Already implemented and working
- ✅ Automatic population by BaseDAOImpl
- ✅ No additional database changes needed
- ✅ Provides basic audit trail

**How to Access**:
```java
// Last update information from KPI itself
Date lastModified = kpi.getDateChanged();
User lastModifiedBy = kpi.getChangedBy();
Date created = kpi.getDateCreated();
User createdBy = kpi.getCreatedBy();
```

**Data Sources**:
- **Last Updated Date**: `selectedKpi.dateChanged` (from BaseEntity)
- **Last Updated By**: `selectedKpi.changedBy.firstName + " " + selectedKpi.changedBy.lastName`
- **Comments**: Not available in this approach

### Option 2: Dedicated Update History Table (Recommended for Detailed Tracking)

**Advantages**:
- ✅ Complete update history with comments
- ✅ Tracks value changes over time
- ✅ Supports detailed analytics and charts
- ✅ Audit trail with business context

**Implementation Status**: ✅ **COMPLETED**

**Data Sources**:
- **Update History**: `KpiUpdateHistory` table via `KpiUpdateHistoryService`
- **Last Updated Date**: `kpiUpdateHistory.updateDate`
- **Last Updated By**: `kpiUpdateHistory.updatedByUser`
- **Comments**: `kpiUpdateHistory.updateComment`

## Updated KpiDetailView Implementation

### Key Changes Made:

1. **Service Integration**:
   ```java
   private KpiUpdateHistoryService kpiUpdateHistoryService;
   ```

2. **Real Data Loading**:
   ```java
   private void loadKpiUpdateHistory() {
       this.kpiUpdateHistory = kpiUpdateHistoryService.getUpdateHistoryByKpi(selectedKpi);
   }
   ```

3. **Dynamic Chart Data**:
   ```java
   private void createChartDataFromHistory() {
       // Uses actual update history instead of random data
   }
   ```

4. **Real Update Tracking**:
   ```java
   public String updateKpiValue() {
       // Creates actual KpiUpdateHistory records
       kpiUpdateHistoryService.createUpdateHistory(selectedKpi, previousValue, newValue, updateComment);
   }
   ```

## Database Schema Requirements

### New Table: `kpi_update_history`
```sql
CREATE TABLE kpi_update_history (
    id VARCHAR(255) PRIMARY KEY,
    kpi_id VARCHAR(255) NOT NULL,
    previous_value DECIMAL(19,2),
    new_value DECIMAL(19,2) NOT NULL,
    update_comment VARCHAR(1000),
    accomplishment_percentage DECIMAL(5,2),
    update_date TIMESTAMP NOT NULL,
    updated_by_user VARCHAR(255) NOT NULL,
    updated_by_staff VARCHAR(255),
    
    -- BaseEntity fields (automatically handled)
    date_created TIMESTAMP,
    date_changed TIMESTAMP,
    created_by VARCHAR(255),
    changed_by VARCHAR(255),
    record_status VARCHAR(50),
    custom_prop_one VARCHAR(255),
    
    FOREIGN KEY (kpi_id) REFERENCES kpis(id),
    FOREIGN KEY (updated_by_user) REFERENCES users(id),
    FOREIGN KEY (updated_by_staff) REFERENCES staffs(id)
);
```

## Frontend Integration

### KpiDetailView.xhtml Updates
- ✅ Added explanatory text about data source
- ✅ Maintained backward compatibility with existing `kpiUpdates` property
- ✅ Enhanced update history display

### Data Flow:
1. **Page Load**: Load KPI and its update history from database
2. **Display**: Show real update history in table and chart
3. **Update**: Create new `KpiUpdateHistory` record when KPI is updated
4. **Refresh**: Reload history to show new update

## Spring Configuration

Ensure the new service is properly configured:

```xml
<!-- Add to applicationContext.xml if not using component scanning -->
<bean id="kpiUpdateHistoryService" 
      class="org.pahappa.systems.kpiTracker.core.services.kpis.impl.KpiUpdateHistoryServiceImpl" />
```

## Migration Strategy

### Phase 1: Deploy New Model and Services
1. ✅ Deploy `KpiUpdateHistory` entity
2. ✅ Deploy `KpiUpdateHistoryService`
3. ✅ Update `KpiDetailView` to use real data

### Phase 2: Data Population (Future)
1. Create migration script to populate initial history from existing KPIs
2. Set up automated tracking for all KPI updates

### Phase 3: Enhanced Features (Future)
1. Add bulk import capabilities for historical data
2. Implement advanced analytics based on update history
3. Add notification systems for KPI updates

## Benefits of This Implementation

1. **Real Audit Trail**: Complete history of who changed what and when
2. **Business Context**: Comments provide reasoning for updates
3. **Analytics Ready**: Historical data enables trend analysis
4. **Scalable**: Can easily add more tracking fields as needed
5. **Backwards Compatible**: Existing XHTML continues to work

## Testing the Implementation

### Verify Data Sources:
1. **Update a KPI** through the UI
2. **Check Database**: Verify new record in `kpi_update_history` table
3. **View History**: Confirm update appears in the update history table
4. **Check Chart**: Verify chart shows real data progression

### Expected Results:
- Update history shows actual database records
- Charts display real KPI progression over time  
- Comments are preserved and displayed
- User information is correctly captured

## Conclusion

The implementation now provides **dynamic, database-driven KPI update tracking** instead of mock data. The `KpiUpdateHistory` entity and associated services provide a complete audit trail with business context, enabling better KPI management and analytics.

**Data Sources Summary**:
- ✅ **Last Updated Date**: From `KpiUpdateHistory.updateDate`
- ✅ **Last Updated By**: From `KpiUpdateHistory.updatedByUser`
- ✅ **Comments**: From `KpiUpdateHistory.updateComment`
- ✅ **Value History**: Complete progression from `KpiUpdateHistory` records
- ✅ **Charts**: Generated from actual historical data
