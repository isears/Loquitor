import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Defines all commands accessible by the client.
 * IMPORTANT: This class was built to be easily extensible. An additional command can be added by simply adding a public
 * function to this class. All command functions, however, must be of the form:
 *
 * public static String <function name>(ServerState currState, String username){
 *     <do something>
 *
 *     return <function output>
 * }
 *
 * Refer to existing command functions for examples of how to correctly implement a command function.
 */
public class ServerCommands {
    static String[] NON_COMMAND_METHODS = {
            "handle",
            "wait",
            "equals",
            "toString",
            "hashCode",
            "getClass",
            "notify",
            "notifyAll",
            "isCommand"
    };

    /**
     * The main command method. This method is called whenever a command is received from the client, but it is NOT in
     * and of itself, a command function (i.e., the client cannot call the 'handle' command). This method examines the
     * ServerCommands class and attempts to find the function specified by the client. If a function exists that matches
     * the name specified by the client, that function is called with arguments ServerState and userName. If the
     * function does not exist, this method returns the null string, which is handled by the clientThread
     *
     * @param inCommand the command passed from the client thread
     * @param currState a reference to the global server state
     * @param userName the username of the client that sent the command
     * @return if a valid command, the return of the command function identified by the inCommand argument. Otherwise,
     *          null.
     */
    public static String handle(String inCommand, ServerState currState, String userName){
        Method[] methods = ServerCommands.class.getMethods();

        for(Method m : methods){
            if(isCommandMethod(m.getName())){

                if(m.getName().equals(inCommand)){
                    try{
                        return m.invoke(new Object(), currState, userName).toString();
                    }catch(IllegalAccessException e){
                        System.out.println("Illegal Access Exception");
                        return null;
                    }catch(InvocationTargetException e){
                        System.out.println("Invocation Target Exception");
                        return null;
                    }catch(IllegalArgumentException e){
                        System.out.println(
                                "The requested command method, '" +
                                m.getName() +
                                "' was not implemented correctly. " +
                                "please check that it takes the correct number and type of arguments"
                        );

                        return "<<Server Error: command is not functioning properly>>";
                    }

                }
            }

        }
        return null;
    }

    @SuppressWarnings("unused")
    /**
     * Alerts the client that the connection is being closed. Connection tear-down operations after the quit command are
     * handled in the client thread, not as a command function.
     */
    public static String quit(ServerState currState, String userName){
        return "<<Closing connection...>>";
    }

    @SuppressWarnings("unused")
    /**
     * Lists all commands available to the client
     */
    public static String help(ServerState currState, String userName){
        String ret = "Server Commands: " + '\n';
        Method[] methods = ServerCommands.class.getMethods();

        for(Method m : methods){
            if(isCommandMethod(m.getName())){
                ret += ">>" + m.getName() + '\n';
            }
        }

        return ret;
    }

    @SuppressWarnings("unused")
    /**
     * Returns the standard epoch time (this function can be expanded to print formatted dates, as well)
     */
    public static String time(ServerState currState, String userName){
        return ("Server Time: " + System.currentTimeMillis() +'\n');
    }

    @SuppressWarnings("unused")
    /**
     * Lists all members currently registered in the global server state
     */
    public static String viewMembers(ServerState currState, String userName){
        String ret = "Active Members:" + '\n';
        String[] activeUsers = currState.getActiveUsers();

        for(String user : activeUsers){
            ret += user + '\n';
        }
        return ret;
    }

    /**
     * Determines if a string maps to a valid command within the ServerCommands class. Some strings match methods within
     * this class that are not actually valid command methods, such as 'handle', which refers to the handle method,
     * which is obviously not a valid command. Also, built in Java methods such as 'wait', 'equals', and 'toString' are
     * obviously not valid commands.
     *
     * @param method the command
     * @return true if the command maps to a command method, false otherwise
     */
    private static boolean isCommandMethod(String method){
        for(String nonCmd : NON_COMMAND_METHODS){
            if(method.equals(nonCmd)){
                return false;
            }
        }
        return true;
    }

}
