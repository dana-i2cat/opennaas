--
-- PostgreSQL database dump
--

SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

--
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA public IS 'Standard public schema';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: ipnetwork; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE ipnetwork (
    id character varying(255) NOT NULL,
    name character varying(255),
    connectiontype character varying(255)
);


ALTER TABLE public.ipnetwork OWNER TO postgres;

--
-- Name: ipnetwork_networkgraph; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE ipnetwork_networkgraph (
    ipnetwork_key character varying(255) NOT NULL,
    sourcerouterkey character varying(255),
    sourceinterfaceid character varying(255),
    sourcesubinterfaceid character varying(255),
    sourceanchor character varying(255),
    targetrouterkey character varying(255),
    targetinterfaceid character varying(255),
    targetsubinterfaceid character varying(255),
    targetanchor character varying(255),
    "position" integer NOT NULL
);


ALTER TABLE public.ipnetwork_networkgraph OWNER TO postgres;

--
-- Name: ipnetwork_routers; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE ipnetwork_routers (
    ipnetwork_key character varying(255) NOT NULL,
    "key" character varying(255),
    host character varying(255),
    "position" integer NOT NULL
);


ALTER TABLE public.ipnetwork_routers OWNER TO postgres;

--
-- Name: ipnetwork_routes; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE ipnetwork_routes (
    ipnetwork_key character varying(255) NOT NULL,
    routes character varying(255),
    "position" integer NOT NULL
);


ALTER TABLE public.ipnetwork_routes OWNER TO postgres;

--
-- Name: ipnetwork_subnetworks; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE ipnetwork_subnetworks (
    ipnetwork_key character varying(255) NOT NULL,
    subnetworks character varying(255),
    "position" integer NOT NULL
);


ALTER TABLE public.ipnetwork_subnetworks OWNER TO postgres;

--
-- Name: ipnetwork_vpns; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE ipnetwork_vpns (
    ipnetwork_key character varying(255) NOT NULL,
    vpns character varying(255),
    "position" integer NOT NULL
);


ALTER TABLE public.ipnetwork_vpns OWNER TO postgres;

--
-- Name: policies_context; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE policies_context (
    policy_key character varying(255) NOT NULL,
    context_key character varying(255),
    "position" integer NOT NULL
);


ALTER TABLE public.policies_context OWNER TO postgres;

--
-- Name: policies_organizationsallowed; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE policies_organizationsallowed (
    policy_key character varying(255) NOT NULL,
    organizations_allowed character varying(255),
    "position" integer NOT NULL
);


ALTER TABLE public.policies_organizationsallowed OWNER TO postgres;

--
-- Name: policies_usersallowed; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE policies_usersallowed (
    policy_key character varying(255) NOT NULL,
    users_allowed character varying(255),
    "position" integer NOT NULL
);


ALTER TABLE public.policies_usersallowed OWNER TO postgres;

--
-- Name: policy; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE policy (
    policy_key character varying(255) NOT NULL,
    operation character varying(255),
    resource_key character varying(255)
);


ALTER TABLE public.policy OWNER TO postgres;

--
-- Name: router_instance; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE router_instance (
    router_resource_id character varying(255) NOT NULL,
    router_name character varying(255),
    model character varying(255),
    parent character varying(255),
    polling_period bigint,
    "location" integer,
    accessconfiguration integer
);


ALTER TABLE public.router_instance OWNER TO postgres;

--
-- Name: router_instance_access_configuration; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE router_instance_access_configuration (
    access_configuration_id integer NOT NULL,
    ip_access_address character varying(255),
    port integer,
    transport_name character varying(255),
    protocol_name character varying(255)
);


ALTER TABLE public.router_instance_access_configuration OWNER TO postgres;

--
-- Name: router_instance_children; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE router_instance_children (
    router_instance_id character varying(255) NOT NULL,
    children_key character varying(255),
    "position" integer NOT NULL
);


ALTER TABLE public.router_instance_children OWNER TO postgres;

--
-- Name: router_instance_location_type; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE router_instance_location_type (
    location_id integer NOT NULL,
    city character varying(255),
    country character varying(255),
    address character varying(255),
    telephone character varying(255),
    email character varying(255),
    latitude double precision,
    longitude double precision,
    time_zone character varying(255)
);


ALTER TABLE public.router_instance_location_type OWNER TO postgres;

--
-- Name: router_instance_user_account; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE router_instance_user_account (
    user_account_id integer NOT NULL,
    user_id character varying(255),
    psw character varying(255),
    privileged_user character varying(255),
    privileged_psw character varying(255),
    router_configured character varying(255),
    smtp_server character varying(255),
    smtp_server_port character varying(255),
    email_user character varying(255),
    email_password character varying(255),
    router_instance_id character varying(255)
);


ALTER TABLE public.router_instance_user_account OWNER TO postgres;

--
-- Name: user_instance; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE user_instance (
    resource_id character varying(255) NOT NULL,
    user_name character varying(255),
    first_name character varying(255),
    last_name character varying(255),
    organization character varying(255),
    address character varying(255),
    telephone character varying(255),
    email character varying(255),
    "role" character varying(255)
);


ALTER TABLE public.user_instance OWNER TO postgres;

--
-- Data for Name: ipnetwork; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ipnetwork (id, name, connectiontype) FROM stdin;
\.


--
-- Data for Name: ipnetwork_networkgraph; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ipnetwork_networkgraph (ipnetwork_key, sourcerouterkey, sourceinterfaceid, sourcesubinterfaceid, sourceanchor, targetrouterkey, targetinterfaceid, targetsubinterfaceid, targetanchor, "position") FROM stdin;
\.


--
-- Data for Name: ipnetwork_routers; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ipnetwork_routers (ipnetwork_key, "key", host, "position") FROM stdin;
\.


--
-- Data for Name: ipnetwork_routes; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ipnetwork_routes (ipnetwork_key, routes, "position") FROM stdin;
\.


--
-- Data for Name: ipnetwork_subnetworks; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ipnetwork_subnetworks (ipnetwork_key, subnetworks, "position") FROM stdin;
\.


--
-- Data for Name: ipnetwork_vpns; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY ipnetwork_vpns (ipnetwork_key, vpns, "position") FROM stdin;
\.


--
-- Data for Name: policies_context; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY policies_context (policy_key, context_key, "position") FROM stdin;
40211f70-335a-11df-aebd-e67586aa94e9		0
4038ed30-335a-11df-aebd-e67586aa94e9		0
403d5a00-335a-11df-aebd-e67586aa94e9		0
\.


--
-- Data for Name: policies_organizationsallowed; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY policies_organizationsallowed (policy_key, organizations_allowed, "position") FROM stdin;
40211f70-335a-11df-aebd-e67586aa94e9	ITI	0
4038ed30-335a-11df-aebd-e67586aa94e9	ITI	0
403d5a00-335a-11df-aebd-e67586aa94e9	ITI	0
\.


--
-- Data for Name: policies_usersallowed; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY policies_usersallowed (policy_key, users_allowed, "position") FROM stdin;
40211f70-335a-11df-aebd-e67586aa94e9		0
4038ed30-335a-11df-aebd-e67586aa94e9		0
403d5a00-335a-11df-aebd-e67586aa94e9		0
\.


--
-- Data for Name: policy; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY policy (policy_key, operation, resource_key) FROM stdin;
40211f70-335a-11df-aebd-e67586aa94e9	create:userManagement	
4038ed30-335a-11df-aebd-e67586aa94e9	create:router	
403d5a00-335a-11df-aebd-e67586aa94e9	create:ipnetwork	
\.


--
-- Data for Name: router_instance; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY router_instance (router_resource_id, router_name, model, parent, polling_period, "location", accessconfiguration) FROM stdin;
\.


--
-- Data for Name: router_instance_access_configuration; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY router_instance_access_configuration (access_configuration_id, ip_access_address, port, transport_name, protocol_name) FROM stdin;
\.


--
-- Data for Name: router_instance_children; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY router_instance_children (router_instance_id, children_key, "position") FROM stdin;
\.


--
-- Data for Name: router_instance_location_type; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY router_instance_location_type (location_id, city, country, address, telephone, email, latitude, longitude, time_zone) FROM stdin;
\.


--
-- Data for Name: router_instance_user_account; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY router_instance_user_account (user_account_id, user_id, psw, privileged_user, privileged_psw, router_configured, smtp_server, smtp_server_port, email_user, email_password, router_instance_id) FROM stdin;
\.


--
-- Data for Name: user_instance; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY user_instance (resource_id, user_name, first_name, last_name, organization, address, telephone, email, "role") FROM stdin;
3e5d0140-335a-11df-aebd-e67586aa94e9	admin	Administrator	Account	ITI	\N	\N	\N	Default
\.


--
-- Name: ipnetwork_networkgraph_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY ipnetwork_networkgraph
    ADD CONSTRAINT ipnetwork_networkgraph_pkey PRIMARY KEY (ipnetwork_key, "position");


--
-- Name: ipnetwork_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY ipnetwork
    ADD CONSTRAINT ipnetwork_pkey PRIMARY KEY (id);


--
-- Name: ipnetwork_routers_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY ipnetwork_routers
    ADD CONSTRAINT ipnetwork_routers_pkey PRIMARY KEY (ipnetwork_key, "position");


--
-- Name: ipnetwork_routes_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY ipnetwork_routes
    ADD CONSTRAINT ipnetwork_routes_pkey PRIMARY KEY (ipnetwork_key, "position");


--
-- Name: ipnetwork_subnetworks_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY ipnetwork_subnetworks
    ADD CONSTRAINT ipnetwork_subnetworks_pkey PRIMARY KEY (ipnetwork_key, "position");


--
-- Name: ipnetwork_vpns_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY ipnetwork_vpns
    ADD CONSTRAINT ipnetwork_vpns_pkey PRIMARY KEY (ipnetwork_key, "position");


--
-- Name: policies_context_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY policies_context
    ADD CONSTRAINT policies_context_pkey PRIMARY KEY (policy_key, "position");


--
-- Name: policies_organizationsallowed_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY policies_organizationsallowed
    ADD CONSTRAINT policies_organizationsallowed_pkey PRIMARY KEY (policy_key, "position");


--
-- Name: policies_usersallowed_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY policies_usersallowed
    ADD CONSTRAINT policies_usersallowed_pkey PRIMARY KEY (policy_key, "position");


--
-- Name: policy_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY policy
    ADD CONSTRAINT policy_pkey PRIMARY KEY (policy_key);


--
-- Name: router_instance_access_configuration_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY router_instance_access_configuration
    ADD CONSTRAINT router_instance_access_configuration_pkey PRIMARY KEY (access_configuration_id);


--
-- Name: router_instance_accessconfiguration_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY router_instance
    ADD CONSTRAINT router_instance_accessconfiguration_key UNIQUE (accessconfiguration);


--
-- Name: router_instance_children_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY router_instance_children
    ADD CONSTRAINT router_instance_children_pkey PRIMARY KEY (router_instance_id, "position");


--
-- Name: router_instance_location_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY router_instance
    ADD CONSTRAINT router_instance_location_key UNIQUE ("location");


--
-- Name: router_instance_location_type_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY router_instance_location_type
    ADD CONSTRAINT router_instance_location_type_pkey PRIMARY KEY (location_id);


--
-- Name: router_instance_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY router_instance
    ADD CONSTRAINT router_instance_pkey PRIMARY KEY (router_resource_id);


--
-- Name: router_instance_user_account_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY router_instance_user_account
    ADD CONSTRAINT router_instance_user_account_pkey PRIMARY KEY (user_account_id);


--
-- Name: user_instance_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY user_instance
    ADD CONSTRAINT user_instance_pkey PRIMARY KEY (resource_id);


--
-- Name: fk1e92cd0f6f639971; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY policies_usersallowed
    ADD CONSTRAINT fk1e92cd0f6f639971 FOREIGN KEY (policy_key) REFERENCES policy(policy_key);


--
-- Name: fk54d7653228593f8; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ipnetwork_routers
    ADD CONSTRAINT fk54d7653228593f8 FOREIGN KEY (ipnetwork_key) REFERENCES ipnetwork(id);


--
-- Name: fk7659876228593f8; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ipnetwork_routes
    ADD CONSTRAINT fk7659876228593f8 FOREIGN KEY (ipnetwork_key) REFERENCES ipnetwork(id);


--
-- Name: fk7d60a48da0553373; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY router_instance_user_account
    ADD CONSTRAINT fk7d60a48da0553373 FOREIGN KEY (router_instance_id) REFERENCES router_instance(router_resource_id);


--
-- Name: fk8575c92d28593f8; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ipnetwork_subnetworks
    ADD CONSTRAINT fk8575c92d28593f8 FOREIGN KEY (ipnetwork_key) REFERENCES ipnetwork(id);


--
-- Name: fk8fc17dc06f639971; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY policies_context
    ADD CONSTRAINT fk8fc17dc06f639971 FOREIGN KEY (policy_key) REFERENCES policy(policy_key);


--
-- Name: fka66321d828593f8; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ipnetwork_networkgraph
    ADD CONSTRAINT fka66321d828593f8 FOREIGN KEY (ipnetwork_key) REFERENCES ipnetwork(id);


--
-- Name: fkc4e0f6f3a0553373; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY router_instance_children
    ADD CONSTRAINT fkc4e0f6f3a0553373 FOREIGN KEY (router_instance_id) REFERENCES router_instance(router_resource_id);


--
-- Name: fkc75ce7776f639971; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY policies_organizationsallowed
    ADD CONSTRAINT fkc75ce7776f639971 FOREIGN KEY (policy_key) REFERENCES policy(policy_key);


--
-- Name: fke9d0cccb4e62ca7a; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY router_instance
    ADD CONSTRAINT fke9d0cccb4e62ca7a FOREIGN KEY (accessconfiguration) REFERENCES router_instance_access_configuration(access_configuration_id);


--
-- Name: fke9d0cccb7c6ed750; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY router_instance
    ADD CONSTRAINT fke9d0cccb7c6ed750 FOREIGN KEY ("location") REFERENCES router_instance_location_type(location_id);


--
-- Name: fkfc226c1728593f8; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ipnetwork_vpns
    ADD CONSTRAINT fkfc226c1728593f8 FOREIGN KEY (ipnetwork_key) REFERENCES ipnetwork(id);


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

