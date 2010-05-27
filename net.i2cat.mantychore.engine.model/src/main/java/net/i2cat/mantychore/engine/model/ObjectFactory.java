package net.i2cat.mantychore.engine.model;



public class ObjectFactory {


    public ObjectFactory() {
    }

    public AccessConfigurationType createAccessConfigurationType() {
        return new AccessConfigurationType();
    }

    public ParametersType.Parameter createParametersTypeParameter() {
        return new ParametersType.Parameter();
    }

    public RouterType createRouterType() {
        return new RouterType();
    }

    public UserAccountType createUserAccountType() {
        return new UserAccountType();
    }


    public LocationType createLocationType() {
        return new LocationType();
    }

    public SubInterfaceType createSubInterfaceType() {
        return new SubInterfaceType();
    }

    public IPConfigurationType createIPConfigurationType() {
        return new IPConfigurationType();
    }


    public ParametersType createParametersType() {
        return new ParametersType();
    }


    public PhysicalInterfaceType createPhysicalInterfaceType() {
        return new PhysicalInterfaceType();
    }

}
