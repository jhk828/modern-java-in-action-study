public class EnumTest {
    public enum Day {
        MON("Monday"),
        TUE("Tuesday"),
        WED("Wednesday"),
        THU("Thursday"),
        FRI("Friday"),
        SAT("Saturday"),
        SUN("Sunday")
        ;

        private final String label;

        Day(String label) {
            this.label = label;
        }

        public String label() {
            return label;
        }

        public static void main(String[] args) {
            System.out.println(Day.MON.name());
            System.out.println(Day.MON.label());
        }
    }
}
