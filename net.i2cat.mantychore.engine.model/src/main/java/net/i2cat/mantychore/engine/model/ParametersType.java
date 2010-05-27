package net.i2cat.mantychore.engine.model;


import java.util.ArrayList;
import java.util.List;



public class ParametersType {


    protected List<ParametersType.Parameter> parameter;

    public List<ParametersType.Parameter> getParameter() {
        if (parameter == null) {
            parameter = new ArrayList<ParametersType.Parameter>();
        }
        return this.parameter;
    }


    public static class Parameter {
        protected String name;
        protected String value;
        protected String description;
 
        public String getName() {
            return name;
        }

        public void setName(String value) {
            this.name = value;
        }


        public String getValue() {
            return value;
        }


        public void setValue(String value) {
            this.value = value;
        }


        public String getDescription() {
            return description;
        }

        public void setDescription(String value) {
            this.description = value;
        }

    }

}
