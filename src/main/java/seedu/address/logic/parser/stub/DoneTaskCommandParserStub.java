package seedu.address.logic.parser.stub;

/*import static java.util.Objects.requireNonNull;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TASK_INDEX;*/

<<<<<<< HEAD:src/main/java/seedu/address/logic/parser/stub/DoneTaskCommandParserStub.java
import java.util.Collection;
=======
import java.time.LocalDateTime;

/*import java.util.Collection;
>>>>>>> d2e10e7a9051df4792c19bd6fab68b1beb71392e:src/main/java/seedu/address/logic/parser/SetDeadlineCommandParserStub.java
import java.util.Collections;
import java.util.Optional;
import java.util.Set;*/

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DoneTaskCommand;
import seedu.address.logic.parser.exceptions.ParseException;
<<<<<<< HEAD:src/main/java/seedu/address/logic/parser/stub/DoneTaskCommandParserStub.java
import seedu.address.logic.parser.Parser;
import seedu.address.model.tag.Tag;
=======
//import seedu.address.model.tag.Tag;
>>>>>>> d2e10e7a9051df4792c19bd6fab68b1beb71392e:src/main/java/seedu/address/logic/parser/SetDeadlineCommandParserStub.java

/**
 * Parses input arguments and creates a new EditCommand object
 */
public class DoneTaskCommandParserStub implements Parser<DoneTaskCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the EditCommand
     * and returns an EditCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public DoneTaskCommand parse(String args) throws ParseException {
        Index stubIndex1 = Index.fromOneBased(3);
        return new DoneTaskCommand(stubIndex1);
    }
}
