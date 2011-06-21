package peaceman.peacecontrol;

/**
 *
 * @author Naegele.Nico
 */
public class ObjectProperty<A> {
    private String name;
    private A property;
    
    public ObjectProperty(String name, A property) {
        this.name = name;
        this.property = property;
    }
    
    public String getName() {
        return this.name;
    }
    
    public A getProperty() {
        return this.property;
    }
}
