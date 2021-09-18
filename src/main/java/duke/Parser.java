package duke;

import duke.command.Command;
import duke.command.AddCommand;
import duke.command.DeleteCommand;
import duke.command.DoneCommand;
import duke.command.ExitCommand;
import duke.command.HelpCommand;
import duke.command.ListCommand;
import duke.task.Deadline;
import duke.task.Event;
import duke.task.Todo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Parser {

    private static final int COMMAND_KEYWORD = 0;

    private static final int STRING_LENGTH_DONE = 4;
    private static final int STRING_LENGTH_TODO = 4;
    private static final int STRING_LENGTH_DEADLINE = 8;
    private static final int STRING_LENGTH_EVENT = 5;
    private static final int STRING_LENGTH_DELETE = 6;
    private static final int STRING_LENGTH_BY_INDICATOR = 3;
    private static final int STRING_LENGTH_AT_INDICATOR = 3;
    private static final int STRING_LENGTH_DATE_FORMAT = 15;

    private static final int TASK_NUMBER = 1;

    private static final String BY_INDICATOR = "/by";
    private static final String AT_INDICATOR = "/at";

    private static final String LS = System.lineSeparator();
    private static final String S_TAB = "     ";
    private static final String NL = LS + S_TAB;

    private static final String COMMAND_BYE = "bye";
    private static final String COMMAND_LIST = "list";
    private static final String COMMAND_DONE = "done";
    private static final String COMMAND_TODO = "todo";
    private static final String COMMAND_DEADLINE = "deadline";
    private static final String COMMAND_EVENT = "event";
    private static final String COMMAND_HELP = "help";
    private static final String COMMAND_DELETE = "delete";

    private static final String FORMAT_DONE = "Command format: 'done <TASK_NUMBER>'";
    private static final String FORMAT_TODO = "Command format: 'todo <TASK_NAME>'";
    private static final String FORMAT_DEADLINE = "Command format: 'deadline <TASK_NAME> /by <TASK_DATE>'";
    private static final String FORMAT_EVENT = "Command format: 'event <TASK_NAME> /at <TASK_DATE>'";
    private static final String FORMAT_DELETE = "Command format: 'delete <TASK_NAME>'";

    private static final String ERROR_DEFAULT = S_TAB + "Invalid command. Type 'help' to see a list of commands.";
    private static final String ERROR_BYE = S_TAB + "ERROR: Type 'bye' without additional parameters to exit.";
    private static final String ERROR_LIST = S_TAB + "ERROR: Type 'list' without additional parameters to print list.";
    private static final String ERROR_DONE_1 = S_TAB + "ERROR: Provide the task number of the task." + NL + FORMAT_DONE;
    private static final String ERROR_DONE_2 = S_TAB + "ERROR: Provide the task number of one task only." + NL + FORMAT_DONE;
    private static final String ERROR_TODO = S_TAB + "ERROR: Specify a todo task to be added." + NL + FORMAT_TODO;
    private static final String ERROR_DEADLINE_1 = S_TAB + "ERROR: Specify a task and due date." + NL + FORMAT_DEADLINE;
    private static final String ERROR_DEADLINE_2 = S_TAB + "ERROR: Specify due date after /by." + NL + FORMAT_DEADLINE;
    private static final String ERROR_DEADLINE_3 = S_TAB + "ERROR: Specify a deadline name." + NL + FORMAT_DEADLINE;
    private static final String ERROR_DEADLINE_4 = S_TAB + "ERROR: Specify a due date." + NL + FORMAT_DEADLINE;
    private static final String ERROR_EVENT_1 = S_TAB + "ERROR: Specify a task and event date." + NL + FORMAT_EVENT;
    private static final String ERROR_EVENT_2 = S_TAB + "ERROR: Specify event date after /at." + NL + FORMAT_EVENT;
    private static final String ERROR_EVENT_3 = S_TAB + "ERROR: Specify an event name." + NL + FORMAT_EVENT;
    private static final String ERROR_EVENT_4 = S_TAB + "ERROR: Specify an event date." + NL + FORMAT_EVENT;
    private static final String ERROR_HELP = S_TAB + "ERROR: Type 'help' without additional parameters to view all commands.";
    private static final String ERROR_DELETE_1 = S_TAB + "ERROR: Provide the task number of the task." + NL + FORMAT_DELETE;
    private static final String ERROR_DELETE_2 = S_TAB + "ERROR: Provide the task number of one task only." + NL + FORMAT_DELETE;
    private static final String ERROR_INVALID_DATE_FORMAT = S_TAB + "ERROR: Use the following format: 'dd/MM/yyyy HHmm'";
    private static final String ERROR_INVALID_DATE_TIME = S_TAB + "ERROR: Date has to be after the current date.";

    /**
     * Takes in the user input command and breaks down the data.
     *
     * @param inputCommand User input command.
     * @return Command subclass that corresponds to input command.
     * @throws DukeException If non-standard input command is detected.
     */
    public static Command parse(String inputCommand) throws DukeException {
        String[] words = inputCommand.split(" ");
        switch (words[COMMAND_KEYWORD]) {
        case COMMAND_BYE:
            return parseByeCommand(inputCommand);
        case COMMAND_LIST:
            return parseListCommand(inputCommand);
        case COMMAND_DONE:
            return parseDoneCommand(inputCommand, words);
        case COMMAND_TODO:
            return parseTodoCommand(inputCommand);
        case COMMAND_DEADLINE:
            return parseDeadlineCommand(inputCommand, words);
        case COMMAND_EVENT:
            return parseEventCommand(inputCommand, words);
        case COMMAND_HELP:
            return parseHelpCommand(inputCommand);
        case COMMAND_DELETE:
            return parseDeleteCommand(inputCommand, words);
        default:
            throw new DukeException(ERROR_DEFAULT);
        }
    }

    /**
     * Breaks down the data for the exit command.
     *
     * @param inputCommand User input command.
     * @return ExitCommand subclass.
     * @throws DukeException If there are superfluous parameters in the exit command.
     */
    private static Command parseByeCommand(String inputCommand) throws DukeException {
        if (!inputCommand.equals(COMMAND_BYE)) {
            throw new DukeException(ERROR_BYE);
        }
        return new ExitCommand();
    }

    /**
     * Breaks down the data for the list command.
     *
     * @param inputCommand User input command.
     * @return ListCommand subclass.
     * @throws DukeException If there are superfluous parameters in the list command.
     */
    private static Command parseListCommand(String inputCommand) throws DukeException {
        if (!inputCommand.equals(COMMAND_LIST)) {
            throw new DukeException(ERROR_LIST);
        }
        return new ListCommand();
    }

    /**
     * Breaks down the data for the done command.
     *
     * @param inputCommand User input command.
     * @param words        User input command with each word stored in a String array.
     * @return DoneCommand subclass.
     * @throws DukeException If the task number is not specified.
     *                       If there are superfluous parameters in the done command.
     */
    private static Command parseDoneCommand(String inputCommand, String[] words) throws DukeException {
        if (isOnlyKeyword(inputCommand, STRING_LENGTH_DONE)) {
            throw new DukeException(ERROR_DONE_1);
        }
        if (words.length > 2) {
            throw new DukeException(ERROR_DONE_2);
        }
        return new DoneCommand(Integer.parseInt(words[TASK_NUMBER]));
    }

    /**
     * Breaks down the data for the todo command.
     *
     * @param inputCommand User input command.
     * @return AddCommand subclass.
     * @throws DukeException If the task name is not specified.
     */
    private static Command parseTodoCommand(String inputCommand) throws DukeException {
        if (isOnlyKeyword(inputCommand, STRING_LENGTH_TODO)) {
            throw new DukeException(ERROR_TODO);
        }
        String taskName = inputCommand.substring(STRING_LENGTH_TODO + 1);
        return new AddCommand(new Todo(taskName));
    }

    /**
     * Breaks down the data for the deadline command.
     *
     * @param inputCommand User input command.
     * @return AddCommand subclass.
     * @throws DukeException If only the 'deadline' keyword is specified.
     *                       If the '/by' keyword is not specified.
     *                       If the due date is not specified.
     */
    private static Command parseDeadlineCommand(String inputCommand, String[] words) throws DukeException {
        if (isOnlyKeyword(inputCommand, STRING_LENGTH_DEADLINE)) {
            throw new DukeException(ERROR_DEADLINE_1);
        }
        if (!inputCommand.contains(BY_INDICATOR)) {
            throw new DukeException(ERROR_DEADLINE_2);
        }
        if (words[1].equals(BY_INDICATOR)) {
            throw new DukeException(ERROR_DEADLINE_3);
        }
        int startIndexOfByIndicator = inputCommand.indexOf(BY_INDICATOR);
        int startIndexOfDatetime = startIndexOfByIndicator + STRING_LENGTH_BY_INDICATOR + 1;
        if (inputCommand.length() <= startIndexOfDatetime) {
            throw new DukeException(ERROR_DEADLINE_4);
        }
        String taskName = inputCommand.substring(STRING_LENGTH_DEADLINE + 1, startIndexOfByIndicator - 1);
        Date datetime = parseDateTime(inputCommand.substring(startIndexOfDatetime));
        return new AddCommand(new Deadline(taskName, datetime));
    }

    /**
     * Breaks down the data for the event command.
     *
     * @param inputCommand User input command.
     * @return AddCommand subclass.
     * @throws DukeException If only the 'event' keyword is specified.
     *                       If the '/at' keyword is not specified.
     *                       If the due date is not specified.
     */
    private static Command parseEventCommand(String inputCommand, String[] words) throws DukeException {
        if (isOnlyKeyword(inputCommand, STRING_LENGTH_EVENT)) {
            throw new DukeException(ERROR_EVENT_1);
        }
        if (!inputCommand.contains(AT_INDICATOR)) {
            throw new DukeException(ERROR_EVENT_2);
        }
        if (words[1].equals(AT_INDICATOR)) {
            throw new DukeException(ERROR_EVENT_3);
        }
        int startIndexOfAtIndicator = inputCommand.indexOf(AT_INDICATOR);
        int startIndexOfDatetime = startIndexOfAtIndicator + STRING_LENGTH_AT_INDICATOR + 1;
        if (inputCommand.length() <= startIndexOfDatetime) {
            throw new DukeException(ERROR_EVENT_4);
        }
        String taskName = inputCommand.substring(STRING_LENGTH_EVENT + 1, startIndexOfAtIndicator - 1);
        Date datetime = parseDateTime(inputCommand.substring(startIndexOfDatetime));
        return new AddCommand(new Event(taskName, datetime));
    }

    /**
     * Breaks down the data for the help command.
     *
     * @param inputCommand User input command.
     * @return HelpCommand subclass.
     * @throws DukeException If there are superfluous parameters in the help command.
     */
    private static Command parseHelpCommand(String inputCommand) throws DukeException {
        if (!inputCommand.equals(COMMAND_HELP)) {
            throw new DukeException(ERROR_HELP);
        }
        return new HelpCommand();
    }

    /**
     * Breaks down the data for the delete command.
     *
     * @param inputCommand User input command.
     * @param words        User input command with each word stored in a String array.
     * @return DeleteCommand subclass.
     * @throws DukeException If the task number is not specified.
     *                       If there are superfluous parameters in the done command.
     */
    private static Command parseDeleteCommand(String inputCommand, String[] words) throws DukeException {
        if (isOnlyKeyword(inputCommand, STRING_LENGTH_DELETE)) {
            throw new DukeException(ERROR_DELETE_1);
        }
        if (words.length > 2) {
            throw new DukeException(ERROR_DELETE_2);
        }
        return new DeleteCommand(Integer.parseInt(words[TASK_NUMBER]));
    }

    /**
     * Converts the date time string into a Date class.
     *
     * @param dateTime String representation of date time.
     * @return Date class
     * @throws DukeException If the date time string is in the wrong format.
     */
    public static Date parseDateTime(String dateTime) throws DukeException {
        Date date;
        checkLengthDateTime(dateTime);
        try {
            SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy HHmm");
            date = sdfDate.parse(dateTime);
            checkValidDateTime(date);
        } catch (ParseException e) {
            throw new DukeException(ERROR_INVALID_DATE_FORMAT);
        }
        return date;
    }

    /**
     * Checks if the String date time is of the correct length
     *
     * @param dateTime String representation of date time.
     * @throws DukeException If the date time string is of an incorrect length.
     */
    private static void checkLengthDateTime(String dateTime) throws DukeException {
        if (dateTime.length() != STRING_LENGTH_DATE_FORMAT) {
            throw new DukeException(ERROR_INVALID_DATE_FORMAT);
        }
    }

    /**
     * Checks if the date time fielded is after the current date time.
     *
     * @param compareDate Date time to be compared against current date time.
     * @throws DukeException If date time is before the current date time.
     */
    private static void checkValidDateTime(Date compareDate) throws DukeException {
        Date todayDate = new Date();
        if (compareDate.compareTo(todayDate) <= 0) {
            throw new DukeException(ERROR_INVALID_DATE_TIME);
        }
    }

    /**
     * Checks if the user command only contains the command keyword.
     *
     * @param inputCommand User input command.
     * @param keywordLength Length of the command keyword.
     * @return True if user input command is less than or equal to keywordLength + one char space.
     *         False if user input command is more than keywordLength + one char space.
     */
    private static boolean isOnlyKeyword(String inputCommand, int keywordLength) {
        return inputCommand.length() <= keywordLength + 1;
    }

}