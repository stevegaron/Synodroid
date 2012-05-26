package com.bigpupdev.synodroid.utils;

public enum SortOrder {
    Combined,
    BySeeders;

    /**
     * Returns the SortOrder corresponding to the Enum type name it 
     * has, e.g. <code>SortOrder.fromCode("BySeeders")</code> returns 
     * the <code>SortOrder.BySeeders</code> enumeration value
     * @param orderCode The name of the enum type value
     * @return The corresponding enum type value of sort order
     */
    public static SortOrder fromCode(String orderCode) {
            try {
                    return Enum.valueOf(SortOrder.class, orderCode);
            } catch (Exception e) {
                    return null;
            }
    }
    
}
