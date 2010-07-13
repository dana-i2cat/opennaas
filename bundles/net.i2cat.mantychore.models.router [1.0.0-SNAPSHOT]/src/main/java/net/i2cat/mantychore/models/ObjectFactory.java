package net.i2cat.mantychore.models;



public class ObjectFactory {


    public ObjectFactory() {
    }

    public AccessConfiguration createAccessConfigurationType() {
        return new AccessConfiguration();
    }

    public Parameters.Parameter createParametersTypeParameter() {
        return new Parameters.Parameter();
    }

    public RouterModel createRouterType() {
        return new RouterModel();
    }

    public UserAccount createUserAccountType() {
        return new UserAccount();
    }


    public Location createLocationType() {
        return new Location();
    }

    public SubInterface createSubInterfaceType() {
        return new SubInterface();
    }

    public IPConfiguration createIPConfigurationType() {
        return new IPConfiguration();
    }


    public Parameters createParametersType() {
        return new Parameters();
    }


    public PhysicalInterface createPhysicalInterfaceType() {
        return new PhysicalInterface();
    }

}
