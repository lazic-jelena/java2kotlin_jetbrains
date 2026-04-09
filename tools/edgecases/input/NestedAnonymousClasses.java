package edgecases;

public class NestedAnonymousClasses {
    public Runnable build() {
        return new Runnable() {
            @Override
            public void run() {
                Runnable nested = new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("nested");
                    }
                };
                nested.run();
            }
        };
    }
}
