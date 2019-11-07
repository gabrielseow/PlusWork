 package seedu.pluswork.logic.parser;

 import seedu.pluswork.logic.commands.GeneratePDFCommand;
 import seedu.pluswork.logic.parser.exceptions.ParseException;

 import java.util.stream.Stream;

 import static seedu.pluswork.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
 import static seedu.pluswork.logic.parser.CliSyntax.PREFIX_INVENTORY_PDFTYPE;

 public class GeneratePDFCommandParser implements Parser<GeneratePDFCommand> {

     /**
      * Parses the given {@code String} of arguments in the context of the AddCommand
      * and returns an AddCommand object for execution.
      *
      * @throws ParseException if the user input does not conform the expected format
      */
     public GeneratePDFCommand parse(String args) throws ParseException {
         ArgumentMultimap argMultimap =
                 ArgumentTokenizer.tokenize(args, PREFIX_INVENTORY_PDFTYPE);

         //parse inventory name
         if (!arePrefixesPresent(argMultimap, PREFIX_INVENTORY_PDFTYPE)
                 || !argMultimap.getPreamble().isEmpty()) {
             throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, GeneratePDFCommand.MESSAGE_USAGE));
         }
         String type = ParserUtil.parseName(argMultimap.getValue(PREFIX_INVENTORY_PDFTYPE).get()).toString();

         return new GeneratePDFCommand(type);
     }

     /**
      * Returns true if none of the prefixes contains empty {@code Optional} values in the given
      * {@code ArgumentMultimap}.
      */
     private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
         return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
     }
 }
