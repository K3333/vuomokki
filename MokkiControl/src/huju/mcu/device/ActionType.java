package huju.mcu.device;

public enum ActionType 
{
	UNDEFINED (0),
	COMMAND_REQUEST (1),
	COMMAND_RESPONSE (2),
	TIMED_READING (3),
	TRIGGERED_DATA (4);
	
	private final int actionTypeCode;
	
	ActionType(int actionTypeCode) 
	{
        this.actionTypeCode = actionTypeCode;
    }
    
    public int getActionTypeCode() 
    {
    	return this.actionTypeCode;
    }
    
    public static ActionType getActionType(int actionTypeCode) 
    {
        for (ActionType dt : values()) {
            if (dt.getActionTypeCode() == actionTypeCode) {
                return dt;
            }
    	}
    	return UNDEFINED;
        /*
    	if (actionTypeCode == COMMAND_REQUEST.getActionTypeCode())  return COMMAND_REQUEST;
    	if (actionTypeCode == COMMAND_RESPONSE.getActionTypeCode()) return COMMAND_RESPONSE;
    	if (actionTypeCode == TIMED_READING.getActionTypeCode())    return TIMED_READING;
    	if (actionTypeCode == TRIGGERED_DATA.getActionTypeCode())	return TRIGGERED_DATA;
    	return UNDEFINED;
        */
    }
    
    public static ActionType getActionType(String name)
    {
    	for (ActionType dt : values()) {
    		if (dt.name().equals(name)) {
    			return dt;
    		}
    	}
    	return UNDEFINED;
    }
    
    public String toString() {
        /*
    	if (actionTypeCode == UNDEFINED.getActionTypeCode())          return "UNDEFINED TYPE";
    	if (actionTypeCode == COMMAND_REQUEST.getActionTypeCode())    return "COMMAND: REQUEST";
    	if (actionTypeCode == COMMAND_RESPONSE.getActionTypeCode())   return "COMMAND: RESPONSE";
    	if (actionTypeCode == TIMED_READING.getActionTypeCode())      return "Scheduled reading";
    	if (actionTypeCode == TRIGGERED_DATA.getActionTypeCode())     return "TRIGGERED_DATA";
    	return "Unknown type";
        */
        return name();
    }
}
