import model.MemorisationSpec;
import model.Plan;

public class Application {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java -jar memorisation.jar <input.json> <output.json>");
            System.exit(1);
        }

        String inputFilename = args[0];
        String outputFilename = args[1];

        MemorisationSpec memorisationSpec = InputReader.read(inputFilename);

        Plan plan = buildPlan(memorisationSpec);

        OutputWriter.write(plan, outputFilename);
        System.out.println(plan);
    }

    private static Plan buildPlan(MemorisationSpec memorisationSpec) {
        int maxVersesPerDay = PlanOptimiser.minMaxVersesPerDay(memorisationSpec.getBooks(), memorisationSpec.getDays());

        Plan plan = PlanBuilder.buildFromBooks(memorisationSpec.getBooks(), maxVersesPerDay);
        PlanBuilder.addPsalmsToPlan(plan, memorisationSpec.getPsalms());

        return plan;
    }
}
