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
9c2cf5a0-5795-11df-9093-dbf61b95cf9e		0
9c433cc0-5795-11df-9093-dbf61b95cf9e		0
9c490920-5795-11df-9093-dbf61b95cf9e		0
226cf0c0-5796-11df-9093-dbf61b95cf9e		0
22757c40-5796-11df-9093-dbf61b95cf9e		0
227b96c0-5796-11df-9093-dbf61b95cf9e		0
22822670-5796-11df-9093-dbf61b95cf9e		0
228a3cc0-5796-11df-9093-dbf61b95cf9e		0
22927a20-5796-11df-9093-dbf61b95cf9e		0
7f250e10-5796-11df-9093-dbf61b95cf9e		0
7f2d7280-5796-11df-9093-dbf61b95cf9e		0
7f32c9b0-5796-11df-9093-dbf61b95cf9e		0
7f390b40-5796-11df-9093-dbf61b95cf9e		0
7f3ed7a0-5796-11df-9093-dbf61b95cf9e		0
7f4407c0-5796-11df-9093-dbf61b95cf9e		0
9b38dc30-5796-11df-9093-dbf61b95cf9e		0
9b3ecfa0-5796-11df-9093-dbf61b95cf9e		0
9b475b20-5796-11df-9093-dbf61b95cf9e	CONFIGURE_INTERFACES	0
9b4c3d20-5796-11df-9093-dbf61b95cf9e	DELETE_LOGICAL_INTERFACE	0
9b516d40-5796-11df-9093-dbf61b95cf9e	REFRESH	0
9b57fcf0-5796-11df-9093-dbf61b95cf9e	CONFIGURE_STATUS	0
9b5cdef0-5796-11df-9093-dbf61b95cf9e	ADD_STATIC_ROUTE	0
9b63bcc0-5796-11df-9093-dbf61b95cf9e	DELETE_STATIC_ROUTE	0
9b69d740-5796-11df-9093-dbf61b95cf9e	CONFIGURE_RIP_ROUTE	0
9b6e9230-5796-11df-9093-dbf61b95cf9e	DELETE_RIP_ROUTE	0
9b75e530-5796-11df-9093-dbf61b95cf9e	CONFIGURE_OSPF_ROUTE	0
9b7b8a80-5796-11df-9093-dbf61b95cf9e	DELETE_OSPF_ROUTE	0
9b80e1b0-5796-11df-9093-dbf61b95cf9e	CONFIGURE_RIPNG_ROUTE	0
9b850060-5796-11df-9093-dbf61b95cf9e	DELETE_RIPNG_ROUTE	0
9b8a5790-5796-11df-9093-dbf61b95cf9e	CONFIGURE_OSPFV3_ROUTE	0
9b8e9d50-5796-11df-9093-dbf61b95cf9e	DELETE_OSPFV3_ROUTE	0
9b93a660-5796-11df-9093-dbf61b95cf9e	CONFIGURE_BGP_ROUTE	0
9b98fd90-5796-11df-9093-dbf61b95cf9e	DELETE_BGP_ROUTE	0
9b9d9170-5796-11df-9093-dbf61b95cf9e	CONFIGURE_POLICIES_BGP	0
9ba2e8a0-5796-11df-9093-dbf61b95cf9e	DELETE_POLICIES_BGP	0
9ba866e0-5796-11df-9093-dbf61b95cf9e		0
9bac5e80-5796-11df-9093-dbf61b95cf9e		0
9bb0cb50-5796-11df-9093-dbf61b95cf9e		0
9bb53820-5796-11df-9093-dbf61b95cf9e		0
9bba6840-5796-11df-9093-dbf61b95cf9e		0
9e0d8000-5796-11df-9093-dbf61b95cf9e		0
9e139a80-5796-11df-9093-dbf61b95cf9e		0
9e1966e0-5796-11df-9093-dbf61b95cf9e	CONFIGURE_INTERFACES	0
9e1f3340-5796-11df-9093-dbf61b95cf9e	DELETE_LOGICAL_INTERFACE	0
9e2526b0-5796-11df-9093-dbf61b95cf9e	REFRESH	0
9e2b1a20-5796-11df-9093-dbf61b95cf9e	CONFIGURE_STATUS	0
9e2f86f0-5796-11df-9093-dbf61b95cf9e	ADD_STATIC_ROUTE	0
9e3664c0-5796-11df-9093-dbf61b95cf9e	DELETE_STATIC_ROUTE	0
9e3d4290-5796-11df-9093-dbf61b95cf9e	CONFIGURE_RIP_ROUTE	0
9e41d670-5796-11df-9093-dbf61b95cf9e	DELETE_RIP_ROUTE	0
9e461c30-5796-11df-9093-dbf61b95cf9e	CONFIGURE_OSPF_ROUTE	0
9e4c0fa0-5796-11df-9093-dbf61b95cf9e	DELETE_OSPF_ROUTE	0
9e50ca90-5796-11df-9093-dbf61b95cf9e	CONFIGURE_RIPNG_ROUTE	0
9e551050-5796-11df-9093-dbf61b95cf9e	DELETE_RIPNG_ROUTE	0
9e5a1960-5796-11df-9093-dbf61b95cf9e	CONFIGURE_OSPFV3_ROUTE	0
9e605af0-5796-11df-9093-dbf61b95cf9e	DELETE_OSPFV3_ROUTE	0
9e64a0b0-5796-11df-9093-dbf61b95cf9e	CONFIGURE_BGP_ROUTE	0
9e6982b0-5796-11df-9093-dbf61b95cf9e	DELETE_BGP_ROUTE	0
9e6e8bc0-5796-11df-9093-dbf61b95cf9e	CONFIGURE_POLICIES_BGP	0
9e73e2f0-5796-11df-9093-dbf61b95cf9e	DELETE_POLICIES_BGP	0
9e77b380-5796-11df-9093-dbf61b95cf9e		0
9e7bd230-5796-11df-9093-dbf61b95cf9e		0
9e7fc9d0-5796-11df-9093-dbf61b95cf9e		0
9e865980-5796-11df-9093-dbf61b95cf9e		0
9e8a2a10-5796-11df-9093-dbf61b95cf9e		0
a0aeded0-5796-11df-9093-dbf61b95cf9e		0
a0b4f950-5796-11df-9093-dbf61b95cf9e		0
a0ba0260-5796-11df-9093-dbf61b95cf9e	CONFIGURE_INTERFACES	0
a0c06b00-5796-11df-9093-dbf61b95cf9e	DELETE_LOGICAL_INTERFACE	0
a0c7be00-5796-11df-9093-dbf61b95cf9e	REFRESH	0
a0ce26a0-5796-11df-9093-dbf61b95cf9e	CONFIGURE_STATUS	0
a0d3cbf0-5796-11df-9093-dbf61b95cf9e	ADD_STATIC_ROUTE	0
a0d99850-5796-11df-9093-dbf61b95cf9e	DELETE_STATIC_ROUTE	0
a0dec870-5796-11df-9093-dbf61b95cf9e	CONFIGURE_RIP_ROUTE	0
a0e4bbe0-5796-11df-9093-dbf61b95cf9e	DELETE_RIP_ROUTE	0
a0ea1310-5796-11df-9093-dbf61b95cf9e	CONFIGURE_OSPF_ROUTE	0
a0eea6f0-5796-11df-9093-dbf61b95cf9e	DELETE_OSPF_ROUTE	0
a0f33ad0-5796-11df-9093-dbf61b95cf9e	CONFIGURE_RIPNG_ROUTE	0
a0f7f5c0-5796-11df-9093-dbf61b95cf9e	DELETE_RIPNG_ROUTE	0
a0fdc220-5796-11df-9093-dbf61b95cf9e	CONFIGURE_OSPFV3_ROUTE	0
a1025600-5796-11df-9093-dbf61b95cf9e	DELETE_OSPFV3_ROUTE	0
a107ad30-5796-11df-9093-dbf61b95cf9e	CONFIGURE_BGP_ROUTE	0
a10d2b70-5796-11df-9093-dbf61b95cf9e	DELETE_BGP_ROUTE	0
a1123480-5796-11df-9093-dbf61b95cf9e	CONFIGURE_POLICIES_BGP	0
a1178bb0-5796-11df-9093-dbf61b95cf9e	DELETE_POLICIES_BGP	0
a11b3530-5796-11df-9093-dbf61b95cf9e		0
a120da80-5796-11df-9093-dbf61b95cf9e		0
a1248400-5796-11df-9093-dbf61b95cf9e		0
a1282d80-5796-11df-9093-dbf61b95cf9e		0
a12c9a50-5796-11df-9093-dbf61b95cf9e		0
a327f520-5796-11df-9093-dbf61b95cf9e		0
a32fbd50-5796-11df-9093-dbf61b95cf9e		0
a3347840-5796-11df-9093-dbf61b95cf9e	CONFIGURE_INTERFACES	0
a339a860-5796-11df-9093-dbf61b95cf9e	DELETE_LOGICAL_INTERFACE	0
a33e1530-5796-11df-9093-dbf61b95cf9e	REFRESH	0
a34408a0-5796-11df-9093-dbf61b95cf9e	CONFIGURE_STATUS	0
a3484e60-5796-11df-9093-dbf61b95cf9e	ADD_STATIC_ROUTE	0
a34da590-5796-11df-9093-dbf61b95cf9e	DELETE_STATIC_ROUTE	0
a3528790-5796-11df-9093-dbf61b95cf9e	CONFIGURE_RIP_ROUTE	0
a357dec0-5796-11df-9093-dbf61b95cf9e	DELETE_RIP_ROUTE	0
a35cc0c0-5796-11df-9093-dbf61b95cf9e	CONFIGURE_OSPF_ROUTE	0
a361a2c0-5796-11df-9093-dbf61b95cf9e	DELETE_OSPF_ROUTE	0
a3672100-5796-11df-9093-dbf61b95cf9e	CONFIGURE_RIPNG_ROUTE	0
a36b66c0-5796-11df-9093-dbf61b95cf9e	DELETE_RIPNG_ROUTE	0
a36fac80-5796-11df-9093-dbf61b95cf9e	CONFIGURE_OSPFV3_ROUTE	0
a3741950-5796-11df-9093-dbf61b95cf9e	DELETE_OSPFV3_ROUTE	0
a379e5b0-5796-11df-9093-dbf61b95cf9e	CONFIGURE_BGP_ROUTE	0
a37e5280-5796-11df-9093-dbf61b95cf9e	DELETE_BGP_ROUTE	0
a3827130-5796-11df-9093-dbf61b95cf9e	CONFIGURE_POLICIES_BGP	0
a38864a0-5796-11df-9093-dbf61b95cf9e	DELETE_POLICIES_BGP	0
a38dbbd0-5796-11df-9093-dbf61b95cf9e		0
a3916550-5796-11df-9093-dbf61b95cf9e		0
a3964750-5796-11df-9093-dbf61b95cf9e		0
a39b0240-5796-11df-9093-dbf61b95cf9e		0
a39ed2d0-5796-11df-9093-dbf61b95cf9e		0
a59bdb50-5796-11df-9093-dbf61b95cf9e		0
a5a1cec0-5796-11df-9093-dbf61b95cf9e		0
a5a7e940-5796-11df-9093-dbf61b95cf9e	CONFIGURE_INTERFACES	0
a5addcb0-5796-11df-9093-dbf61b95cf9e	DELETE_LOGICAL_INTERFACE	0
a5b3a910-5796-11df-9093-dbf61b95cf9e	REFRESH	0
a5b92750-5796-11df-9093-dbf61b95cf9e	CONFIGURE_STATUS	0
a5be7e80-5796-11df-9093-dbf61b95cf9e	ADD_STATIC_ROUTE	0
a5c905d0-5796-11df-9093-dbf61b95cf9e	DELETE_STATIC_ROUTE	0
a5cd99b0-5796-11df-9093-dbf61b95cf9e	CONFIGURE_RIP_ROUTE	0
a5d2c9d0-5796-11df-9093-dbf61b95cf9e	DELETE_RIP_ROUTE	0
a5d6c170-5796-11df-9093-dbf61b95cf9e	CONFIGURE_OSPF_ROUTE	0
a5dc66c0-5796-11df-9093-dbf61b95cf9e	DELETE_OSPF_ROUTE	0
a5e0ac80-5796-11df-9093-dbf61b95cf9e	CONFIGURE_RIPNG_ROUTE	0
a5e58e80-5796-11df-9093-dbf61b95cf9e	DELETE_RIPNG_ROUTE	0
a5eb33d0-5796-11df-9093-dbf61b95cf9e	CONFIGURE_OSPFV3_ROUTE	0
a5f10030-5796-11df-9093-dbf61b95cf9e	DELETE_OSPFV3_ROUTE	0
a5f59410-5796-11df-9093-dbf61b95cf9e	CONFIGURE_BGP_ROUTE	0
a5f9d9d0-5796-11df-9093-dbf61b95cf9e	DELETE_BGP_ROUTE	0
a600b7a0-5796-11df-9093-dbf61b95cf9e	CONFIGURE_POLICIES_BGP	0
a60c0240-5796-11df-9093-dbf61b95cf9e	DELETE_POLICIES_BGP	0
a60fd2d0-5796-11df-9093-dbf61b95cf9e		0
a613ca70-5796-11df-9093-dbf61b95cf9e		0
a618fa90-5796-11df-9093-dbf61b95cf9e		0
a61cf230-5796-11df-9093-dbf61b95cf9e		0
a620c2c0-5796-11df-9093-dbf61b95cf9e		0
a8365c50-5796-11df-9093-dbf61b95cf9e		0
a83bda90-5796-11df-9093-dbf61b95cf9e		0
a8417fe0-5796-11df-9093-dbf61b95cf9e	CONFIGURE_INTERFACES	0
a8474c40-5796-11df-9093-dbf61b95cf9e	DELETE_LOGICAL_INTERFACE	0
a84d66c0-5796-11df-9093-dbf61b95cf9e	REFRESH	0
a8533320-5796-11df-9093-dbf61b95cf9e	CONFIGURE_STATUS	0
a8583c30-5796-11df-9093-dbf61b95cf9e	ADD_STATIC_ROUTE	0
a85dba70-5796-11df-9093-dbf61b95cf9e	DELETE_STATIC_ROUTE	0
a8627560-5796-11df-9093-dbf61b95cf9e	CONFIGURE_RIP_ROUTE	0
a866bb20-5796-11df-9093-dbf61b95cf9e	DELETE_RIP_ROUTE	0
a86b7610-5796-11df-9093-dbf61b95cf9e	CONFIGURE_OSPF_ROUTE	0
a8711b60-5796-11df-9093-dbf61b95cf9e	DELETE_OSPF_ROUTE	0
a8751300-5796-11df-9093-dbf61b95cf9e	CONFIGURE_RIPNG_ROUTE	0
a87958c0-5796-11df-9093-dbf61b95cf9e	DELETE_RIPNG_ROUTE	0
a87d7770-5796-11df-9093-dbf61b95cf9e	CONFIGURE_OSPFV3_ROUTE	0
a882f5b0-5796-11df-9093-dbf61b95cf9e	DELETE_OSPFV3_ROUTE	0
a8878990-5796-11df-9093-dbf61b95cf9e	CONFIGURE_BGP_ROUTE	0
a88c1d70-5796-11df-9093-dbf61b95cf9e	DELETE_BGP_ROUTE	0
a890d860-5796-11df-9093-dbf61b95cf9e	CONFIGURE_POLICIES_BGP	0
a8956c40-5796-11df-9093-dbf61b95cf9e	DELETE_POLICIES_BGP	0
a898eeb0-5796-11df-9093-dbf61b95cf9e		0
a89d8290-5796-11df-9093-dbf61b95cf9e		0
a8a26490-5796-11df-9093-dbf61b95cf9e		0
a8a5e700-5796-11df-9093-dbf61b95cf9e		0
a8ac76b0-5796-11df-9093-dbf61b95cf9e		0
aa981a10-5796-11df-9093-dbf61b95cf9e		0
aa9bc390-5796-11df-9093-dbf61b95cf9e		0
aad3c380-5796-11df-9093-dbf61b95cf9e	CONFIGURE_INTERFACES	0
aad85760-5796-11df-9093-dbf61b95cf9e	DELETE_LOGICAL_INTERFACE	0
aae06db0-5796-11df-9093-dbf61b95cf9e	REFRESH	0
aae46550-5796-11df-9093-dbf61b95cf9e	CONFIGURE_STATUS	0
aae96e60-5796-11df-9093-dbf61b95cf9e	ADD_STATIC_ROUTE	0
aaeeeca0-5796-11df-9093-dbf61b95cf9e	DELETE_STATIC_ROUTE	0
aaf33260-5796-11df-9093-dbf61b95cf9e	CONFIGURE_RIP_ROUTE	0
aaf75110-5796-11df-9093-dbf61b95cf9e	DELETE_RIP_ROUTE	0
aafc0c00-5796-11df-9093-dbf61b95cf9e	CONFIGURE_OSPF_ROUTE	0
ab002ab0-5796-11df-9093-dbf61b95cf9e	DELETE_OSPF_ROUTE	0
ab044960-5796-11df-9093-dbf61b95cf9e	CONFIGURE_RIPNG_ROUTE	0
ab084100-5796-11df-9093-dbf61b95cf9e	DELETE_RIPNG_ROUTE	0
ab0cfbf0-5796-11df-9093-dbf61b95cf9e	CONFIGURE_OSPFV3_ROUTE	0
ab1168c0-5796-11df-9093-dbf61b95cf9e	DELETE_OSPFV3_ROUTE	0
ab153950-5796-11df-9093-dbf61b95cf9e	CONFIGURE_BGP_ROUTE	0
ab1a4260-5796-11df-9093-dbf61b95cf9e	DELETE_BGP_ROUTE	0
ab1f2460-5796-11df-9093-dbf61b95cf9e	CONFIGURE_POLICIES_BGP	0
ab231c00-5796-11df-9093-dbf61b95cf9e	DELETE_POLICIES_BGP	0
ab269e70-5796-11df-9093-dbf61b95cf9e		0
ab2a47f0-5796-11df-9093-dbf61b95cf9e		0
ab2eb4c0-5796-11df-9093-dbf61b95cf9e		0
ab328550-5796-11df-9093-dbf61b95cf9e		0
ab36a400-5796-11df-9093-dbf61b95cf9e		0
ad5bf500-5796-11df-9093-dbf61b95cf9e		0
ad636f10-5796-11df-9093-dbf61b95cf9e		0
ad685110-5796-11df-9093-dbf61b95cf9e	CONFIGURE_INTERFACES	0
ad6d5a20-5796-11df-9093-dbf61b95cf9e	DELETE_LOGICAL_INTERFACE	0
ad721510-5796-11df-9093-dbf61b95cf9e	REFRESH	0
ad78a4c0-5796-11df-9093-dbf61b95cf9e	CONFIGURE_STATUS	0
ad7cc370-5796-11df-9093-dbf61b95cf9e	ADD_STATIC_ROUTE	0
ad81cc80-5796-11df-9093-dbf61b95cf9e	DELETE_STATIC_ROUTE	0
ad8771d0-5796-11df-9093-dbf61b95cf9e	CONFIGURE_RIP_ROUTE	0
ad8cc900-5796-11df-9093-dbf61b95cf9e	DELETE_RIP_ROUTE	0
ad91f920-5796-11df-9093-dbf61b95cf9e	CONFIGURE_OSPF_ROUTE	0
ad968d00-5796-11df-9093-dbf61b95cf9e	DELETE_OSPF_ROUTE	0
ad9b20e0-5796-11df-9093-dbf61b95cf9e	CONFIGURE_RIPNG_ROUTE	0
ada13b60-5796-11df-9093-dbf61b95cf9e	DELETE_RIPNG_ROUTE	0
ada61d60-5796-11df-9093-dbf61b95cf9e	CONFIGURE_OSPFV3_ROUTE	0
adac5ef0-5796-11df-9093-dbf61b95cf9e	DELETE_OSPFV3_ROUTE	0
adb0cbc0-5796-11df-9093-dbf61b95cf9e	CONFIGURE_BGP_ROUTE	0
adb69820-5796-11df-9093-dbf61b95cf9e	DELETE_BGP_ROUTE	0
adbb04f0-5796-11df-9093-dbf61b95cf9e	CONFIGURE_POLICIES_BGP	0
adc00e00-5796-11df-9093-dbf61b95cf9e	DELETE_POLICIES_BGP	0
adc3de90-5796-11df-9093-dbf61b95cf9e		0
adc87270-5796-11df-9093-dbf61b95cf9e		0
adcdc9a0-5796-11df-9093-dbf61b95cf9e		0
add14c10-5796-11df-9093-dbf61b95cf9e		0
add51ca0-5796-11df-9093-dbf61b95cf9e		0
afff2890-5796-11df-9093-dbf61b95cf9e		0
b0056a20-5796-11df-9093-dbf61b95cf9e		0
b00b3680-5796-11df-9093-dbf61b95cf9e	CONFIGURE_INTERFACES	0
b0103f90-5796-11df-9093-dbf61b95cf9e	DELETE_LOGICAL_INTERFACE	0
b018cb10-5796-11df-9093-dbf61b95cf9e	REFRESH	0
b01e2240-5796-11df-9093-dbf61b95cf9e	CONFIGURE_STATUS	0
b0230440-5796-11df-9093-dbf61b95cf9e	ADD_STATIC_ROUTE	0
b028f7b0-5796-11df-9093-dbf61b95cf9e	DELETE_STATIC_ROUTE	0
b02e9d00-5796-11df-9093-dbf61b95cf9e	CONFIGURE_RIP_ROUTE	0
b033a610-5796-11df-9093-dbf61b95cf9e	DELETE_RIP_ROUTE	0
b037ebd0-5796-11df-9093-dbf61b95cf9e	CONFIGURE_OSPF_ROUTE	0
b03c0a80-5796-11df-9093-dbf61b95cf9e	DELETE_OSPF_ROUTE	0
b04188c0-5796-11df-9093-dbf61b95cf9e	CONFIGURE_RIPNG_ROUTE	0
b0461ca0-5796-11df-9093-dbf61b95cf9e	DELETE_RIPNG_ROUTE	0
b04a8970-5796-11df-9093-dbf61b95cf9e	CONFIGURE_OSPFV3_ROUTE	0
b04f4460-5796-11df-9093-dbf61b95cf9e	DELETE_OSPFV3_ROUTE	0
b055ad00-5796-11df-9093-dbf61b95cf9e	CONFIGURE_BGP_ROUTE	0
b05a19d0-5796-11df-9093-dbf61b95cf9e	DELETE_BGP_ROUTE	0
b05e1170-5796-11df-9093-dbf61b95cf9e	CONFIGURE_POLICIES_BGP	0
b0647a10-5796-11df-9093-dbf61b95cf9e	DELETE_POLICIES_BGP	0
b0698320-5796-11df-9093-dbf61b95cf9e		0
b06dc8e0-5796-11df-9093-dbf61b95cf9e		0
b0720ea0-5796-11df-9093-dbf61b95cf9e		0
b0762d50-5796-11df-9093-dbf61b95cf9e		0
b07b0f50-5796-11df-9093-dbf61b95cf9e		0
b283d7a0-5796-11df-9093-dbf61b95cf9e		0
b288b9a0-5796-11df-9093-dbf61b95cf9e		0
b28cff60-5796-11df-9093-dbf61b95cf9e	CONFIGURE_INTERFACES	0
b2920870-5796-11df-9093-dbf61b95cf9e	DELETE_LOGICAL_INTERFACE	0
b2969c50-5796-11df-9093-dbf61b95cf9e	REFRESH	0
b29ae210-5796-11df-9093-dbf61b95cf9e	CONFIGURE_STATUS	0
b29f4ee0-5796-11df-9093-dbf61b95cf9e	ADD_STATIC_ROUTE	0
b2a56960-5796-11df-9093-dbf61b95cf9e	DELETE_STATIC_ROUTE	0
b2aac090-5796-11df-9093-dbf61b95cf9e	CONFIGURE_RIP_ROUTE	0
b2af0650-5796-11df-9093-dbf61b95cf9e	DELETE_RIP_ROUTE	0
b2b37320-5796-11df-9093-dbf61b95cf9e	CONFIGURE_OSPF_ROUTE	0
b2b82e10-5796-11df-9093-dbf61b95cf9e	DELETE_OSPF_ROUTE	0
b2bc9ae0-5796-11df-9093-dbf61b95cf9e	CONFIGURE_RIPNG_ROUTE	0
b2c0b990-5796-11df-9093-dbf61b95cf9e	DELETE_RIPNG_ROUTE	0
b2c5c2a0-5796-11df-9093-dbf61b95cf9e	CONFIGURE_OSPFV3_ROUTE	0
b2ca2f70-5796-11df-9093-dbf61b95cf9e	DELETE_OSPFV3_ROUTE	0
b2cf3880-5796-11df-9093-dbf61b95cf9e	CONFIGURE_BGP_ROUTE	0
b2d3f370-5796-11df-9093-dbf61b95cf9e	DELETE_BGP_ROUTE	0
b2d83930-5796-11df-9093-dbf61b95cf9e	CONFIGURE_POLICIES_BGP	0
b2dd4240-5796-11df-9093-dbf61b95cf9e	DELETE_POLICIES_BGP	0
b2e1af10-5796-11df-9093-dbf61b95cf9e		0
b2e75460-5796-11df-9093-dbf61b95cf9e		0
b2ead6d0-5796-11df-9093-dbf61b95cf9e		0
b2ee8050-5796-11df-9093-dbf61b95cf9e		0
b2f2ed20-5796-11df-9093-dbf61b95cf9e		0
b4e9db20-5796-11df-9093-dbf61b95cf9e		0
b4efce90-5796-11df-9093-dbf61b95cf9e		0
b4f54cd0-5796-11df-9093-dbf61b95cf9e	CONFIGURE_INTERFACES	0
b4fc9fd0-5796-11df-9093-dbf61b95cf9e	DELETE_LOGICAL_INTERFACE	0
b503f2d0-5796-11df-9093-dbf61b95cf9e	REFRESH	0
b508fbe0-5796-11df-9093-dbf61b95cf9e	CONFIGURE_STATUS	0
b50db6d0-5796-11df-9093-dbf61b95cf9e	ADD_STATIC_ROUTE	0
b5161b40-5796-11df-9093-dbf61b95cf9e	DELETE_STATIC_ROUTE	0
b51aaf20-5796-11df-9093-dbf61b95cf9e	CONFIGURE_RIP_ROUTE	0
b51ef4e0-5796-11df-9093-dbf61b95cf9e	DELETE_RIP_ROUTE	0
b5233aa0-5796-11df-9093-dbf61b95cf9e	CONFIGURE_OSPF_ROUTE	0
b5295520-5796-11df-9093-dbf61b95cf9e	DELETE_OSPF_ROUTE	0
b52e3720-5796-11df-9093-dbf61b95cf9e	CONFIGURE_RIPNG_ROUTE	0
b5336740-5796-11df-9093-dbf61b95cf9e	DELETE_RIPNG_ROUTE	0
b537ad00-5796-11df-9093-dbf61b95cf9e	CONFIGURE_OSPFV3_ROUTE	0
b53ed8f0-5796-11df-9093-dbf61b95cf9e	DELETE_OSPFV3_ROUTE	0
b5436cd0-5796-11df-9093-dbf61b95cf9e	CONFIGURE_BGP_ROUTE	0
b5478b80-5796-11df-9093-dbf61b95cf9e	DELETE_BGP_ROUTE	0
b54b8320-5796-11df-9093-dbf61b95cf9e	CONFIGURE_POLICIES_BGP	0
b550da50-5796-11df-9093-dbf61b95cf9e	DELETE_POLICIES_BGP	0
b55483d0-5796-11df-9093-dbf61b95cf9e		0
b55965d0-5796-11df-9093-dbf61b95cf9e		0
b55d0f50-5796-11df-9093-dbf61b95cf9e		0
b5626680-5796-11df-9093-dbf61b95cf9e		0
b5672170-5796-11df-9093-dbf61b95cf9e		0
b76429f0-5796-11df-9093-dbf61b95cf9e		0
b76a9290-5796-11df-9093-dbf61b95cf9e		0
b76eb140-5796-11df-9093-dbf61b95cf9e	CONFIGURE_INTERFACES	0
b7731e10-5796-11df-9093-dbf61b95cf9e	DELETE_LOGICAL_INTERFACE	0
b77715b0-5796-11df-9093-dbf61b95cf9e	REFRESH	0
b77dcc70-5796-11df-9093-dbf61b95cf9e	CONFIGURE_STATUS	0
b782d580-5796-11df-9093-dbf61b95cf9e	ADD_STATIC_ROUTE	0
b7871b40-5796-11df-9093-dbf61b95cf9e	DELETE_STATIC_ROUTE	0
b78b8810-5796-11df-9093-dbf61b95cf9e	CONFIGURE_RIP_ROUTE	0
b7904300-5796-11df-9093-dbf61b95cf9e	DELETE_RIP_ROUTE	0
b7943aa0-5796-11df-9093-dbf61b95cf9e	CONFIGURE_OSPF_ROUTE	0
b7988060-5796-11df-9093-dbf61b95cf9e	DELETE_OSPF_ROUTE	0
b79d8970-5796-11df-9093-dbf61b95cf9e	CONFIGURE_RIPNG_ROUTE	0
b7a26b70-5796-11df-9093-dbf61b95cf9e	DELETE_RIPNG_ROUTE	0
b7a79b90-5796-11df-9093-dbf61b95cf9e	CONFIGURE_OSPFV3_ROUTE	0
b7b2bf20-5796-11df-9093-dbf61b95cf9e	DELETE_OSPFV3_ROUTE	0
b7b704e0-5796-11df-9093-dbf61b95cf9e	CONFIGURE_BGP_ROUTE	0
b7bc5c10-5796-11df-9093-dbf61b95cf9e	DELETE_BGP_ROUTE	0
b7c02ca0-5796-11df-9093-dbf61b95cf9e	CONFIGURE_POLICIES_BGP	0
b7c3fd30-5796-11df-9093-dbf61b95cf9e	DELETE_POLICIES_BGP	0
b7c89110-5796-11df-9093-dbf61b95cf9e		0
b7cd24f0-5796-11df-9093-dbf61b95cf9e		0
b7d0a760-5796-11df-9093-dbf61b95cf9e		0
b7d402c0-5796-11df-9093-dbf61b95cf9e		0
b7d86f90-5796-11df-9093-dbf61b95cf9e		0
ba162a90-5796-11df-9093-dbf61b95cf9e		0
ba1b5ab0-5796-11df-9093-dbf61b95cf9e		0
ba20b1e0-5796-11df-9093-dbf61b95cf9e	CONFIGURE_INTERFACES	0
ba265730-5796-11df-9093-dbf61b95cf9e	DELETE_LOGICAL_INTERFACE	0
ba2d8320-5796-11df-9093-dbf61b95cf9e	REFRESH	0
ba326520-5796-11df-9093-dbf61b95cf9e	CONFIGURE_STATUS	0
ba3683d0-5796-11df-9093-dbf61b95cf9e	ADD_STATIC_ROUTE	0
ba3b65d0-5796-11df-9093-dbf61b95cf9e	DELETE_STATIC_ROUTE	0
ba413230-5796-11df-9093-dbf61b95cf9e	CONFIGURE_RIP_ROUTE	0
ba4550e0-5796-11df-9093-dbf61b95cf9e	DELETE_RIP_ROUTE	0
ba49bdb0-5796-11df-9093-dbf61b95cf9e	CONFIGURE_OSPF_ROUTE	0
ba4db550-5796-11df-9093-dbf61b95cf9e	DELETE_OSPF_ROUTE	0
ba546c10-5796-11df-9093-dbf61b95cf9e	CONFIGURE_RIPNG_ROUTE	0
ba5ad4b0-5796-11df-9093-dbf61b95cf9e	DELETE_RIPNG_ROUTE	0
ba5f1a70-5796-11df-9093-dbf61b95cf9e	CONFIGURE_OSPFV3_ROUTE	0
ba642380-5796-11df-9093-dbf61b95cf9e	DELETE_OSPFV3_ROUTE	0
ba681b20-5796-11df-9093-dbf61b95cf9e	CONFIGURE_BGP_ROUTE	0
ba6e5cb0-5796-11df-9093-dbf61b95cf9e	DELETE_BGP_ROUTE	0
ba72c980-5796-11df-9093-dbf61b95cf9e	CONFIGURE_POLICIES_BGP	0
ba76c120-5796-11df-9093-dbf61b95cf9e	DELETE_POLICIES_BGP	0
ba7adfd0-5796-11df-9093-dbf61b95cf9e		0
ba7f4ca0-5796-11df-9093-dbf61b95cf9e		0
ba8455b0-5796-11df-9093-dbf61b95cf9e		0
ba87d820-5796-11df-9093-dbf61b95cf9e		0
ba8b81a0-5796-11df-9093-dbf61b95cf9e		0
\.


--
-- Data for Name: policies_organizationsallowed; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY policies_organizationsallowed (policy_key, organizations_allowed, "position") FROM stdin;
9c2cf5a0-5795-11df-9093-dbf61b95cf9e	ITI	0
9c433cc0-5795-11df-9093-dbf61b95cf9e	ITI	0
9c490920-5795-11df-9093-dbf61b95cf9e	ITI	0
226cf0c0-5796-11df-9093-dbf61b95cf9e	ITI	0
22757c40-5796-11df-9093-dbf61b95cf9e	ITI	0
227b96c0-5796-11df-9093-dbf61b95cf9e	ITI	0
22822670-5796-11df-9093-dbf61b95cf9e	ITI	0
228a3cc0-5796-11df-9093-dbf61b95cf9e	ITI	0
22927a20-5796-11df-9093-dbf61b95cf9e	ITI	0
7f250e10-5796-11df-9093-dbf61b95cf9e	ITI	0
7f2d7280-5796-11df-9093-dbf61b95cf9e	ITI	0
7f32c9b0-5796-11df-9093-dbf61b95cf9e	ITI	0
7f390b40-5796-11df-9093-dbf61b95cf9e	ITI	0
7f3ed7a0-5796-11df-9093-dbf61b95cf9e	ITI	0
7f4407c0-5796-11df-9093-dbf61b95cf9e	ITI	0
9b38dc30-5796-11df-9093-dbf61b95cf9e	ITI	0
9b3ecfa0-5796-11df-9093-dbf61b95cf9e	ITI	0
9b475b20-5796-11df-9093-dbf61b95cf9e	ITI	0
9b4c3d20-5796-11df-9093-dbf61b95cf9e	ITI	0
9b516d40-5796-11df-9093-dbf61b95cf9e	ITI	0
9b57fcf0-5796-11df-9093-dbf61b95cf9e	ITI	0
9b5cdef0-5796-11df-9093-dbf61b95cf9e	ITI	0
9b63bcc0-5796-11df-9093-dbf61b95cf9e	ITI	0
9b69d740-5796-11df-9093-dbf61b95cf9e	ITI	0
9b6e9230-5796-11df-9093-dbf61b95cf9e	ITI	0
9b75e530-5796-11df-9093-dbf61b95cf9e	ITI	0
9b7b8a80-5796-11df-9093-dbf61b95cf9e	ITI	0
9b80e1b0-5796-11df-9093-dbf61b95cf9e	ITI	0
9b850060-5796-11df-9093-dbf61b95cf9e	ITI	0
9b8a5790-5796-11df-9093-dbf61b95cf9e	ITI	0
9b8e9d50-5796-11df-9093-dbf61b95cf9e	ITI	0
9b93a660-5796-11df-9093-dbf61b95cf9e	ITI	0
9b98fd90-5796-11df-9093-dbf61b95cf9e	ITI	0
9b9d9170-5796-11df-9093-dbf61b95cf9e	ITI	0
9ba2e8a0-5796-11df-9093-dbf61b95cf9e	ITI	0
9ba866e0-5796-11df-9093-dbf61b95cf9e	ITI	0
9bac5e80-5796-11df-9093-dbf61b95cf9e	ITI	0
9bb0cb50-5796-11df-9093-dbf61b95cf9e	ITI	0
9bb53820-5796-11df-9093-dbf61b95cf9e	ITI	0
9bba6840-5796-11df-9093-dbf61b95cf9e	ITI	0
9e0d8000-5796-11df-9093-dbf61b95cf9e	ITI	0
9e139a80-5796-11df-9093-dbf61b95cf9e	ITI	0
9e1966e0-5796-11df-9093-dbf61b95cf9e	ITI	0
9e1f3340-5796-11df-9093-dbf61b95cf9e	ITI	0
9e2526b0-5796-11df-9093-dbf61b95cf9e	ITI	0
9e2b1a20-5796-11df-9093-dbf61b95cf9e	ITI	0
9e2f86f0-5796-11df-9093-dbf61b95cf9e	ITI	0
9e3664c0-5796-11df-9093-dbf61b95cf9e	ITI	0
9e3d4290-5796-11df-9093-dbf61b95cf9e	ITI	0
9e41d670-5796-11df-9093-dbf61b95cf9e	ITI	0
9e461c30-5796-11df-9093-dbf61b95cf9e	ITI	0
9e4c0fa0-5796-11df-9093-dbf61b95cf9e	ITI	0
9e50ca90-5796-11df-9093-dbf61b95cf9e	ITI	0
9e551050-5796-11df-9093-dbf61b95cf9e	ITI	0
9e5a1960-5796-11df-9093-dbf61b95cf9e	ITI	0
9e605af0-5796-11df-9093-dbf61b95cf9e	ITI	0
9e64a0b0-5796-11df-9093-dbf61b95cf9e	ITI	0
9e6982b0-5796-11df-9093-dbf61b95cf9e	ITI	0
9e6e8bc0-5796-11df-9093-dbf61b95cf9e	ITI	0
9e73e2f0-5796-11df-9093-dbf61b95cf9e	ITI	0
9e77b380-5796-11df-9093-dbf61b95cf9e	ITI	0
9e7bd230-5796-11df-9093-dbf61b95cf9e	ITI	0
9e7fc9d0-5796-11df-9093-dbf61b95cf9e	ITI	0
9e865980-5796-11df-9093-dbf61b95cf9e	ITI	0
9e8a2a10-5796-11df-9093-dbf61b95cf9e	ITI	0
a0aeded0-5796-11df-9093-dbf61b95cf9e	ITI	0
a0b4f950-5796-11df-9093-dbf61b95cf9e	ITI	0
a0ba0260-5796-11df-9093-dbf61b95cf9e	ITI	0
a0c06b00-5796-11df-9093-dbf61b95cf9e	ITI	0
a0c7be00-5796-11df-9093-dbf61b95cf9e	ITI	0
a0ce26a0-5796-11df-9093-dbf61b95cf9e	ITI	0
a0d3cbf0-5796-11df-9093-dbf61b95cf9e	ITI	0
a0d99850-5796-11df-9093-dbf61b95cf9e	ITI	0
a0dec870-5796-11df-9093-dbf61b95cf9e	ITI	0
a0e4bbe0-5796-11df-9093-dbf61b95cf9e	ITI	0
a0ea1310-5796-11df-9093-dbf61b95cf9e	ITI	0
a0eea6f0-5796-11df-9093-dbf61b95cf9e	ITI	0
a0f33ad0-5796-11df-9093-dbf61b95cf9e	ITI	0
a0f7f5c0-5796-11df-9093-dbf61b95cf9e	ITI	0
a0fdc220-5796-11df-9093-dbf61b95cf9e	ITI	0
a1025600-5796-11df-9093-dbf61b95cf9e	ITI	0
a107ad30-5796-11df-9093-dbf61b95cf9e	ITI	0
a10d2b70-5796-11df-9093-dbf61b95cf9e	ITI	0
a1123480-5796-11df-9093-dbf61b95cf9e	ITI	0
a1178bb0-5796-11df-9093-dbf61b95cf9e	ITI	0
a11b3530-5796-11df-9093-dbf61b95cf9e	ITI	0
a120da80-5796-11df-9093-dbf61b95cf9e	ITI	0
a1248400-5796-11df-9093-dbf61b95cf9e	ITI	0
a1282d80-5796-11df-9093-dbf61b95cf9e	ITI	0
a12c9a50-5796-11df-9093-dbf61b95cf9e	ITI	0
a327f520-5796-11df-9093-dbf61b95cf9e	ITI	0
a32fbd50-5796-11df-9093-dbf61b95cf9e	ITI	0
a3347840-5796-11df-9093-dbf61b95cf9e	ITI	0
a339a860-5796-11df-9093-dbf61b95cf9e	ITI	0
a33e1530-5796-11df-9093-dbf61b95cf9e	ITI	0
a34408a0-5796-11df-9093-dbf61b95cf9e	ITI	0
a3484e60-5796-11df-9093-dbf61b95cf9e	ITI	0
a34da590-5796-11df-9093-dbf61b95cf9e	ITI	0
a3528790-5796-11df-9093-dbf61b95cf9e	ITI	0
a357dec0-5796-11df-9093-dbf61b95cf9e	ITI	0
a35cc0c0-5796-11df-9093-dbf61b95cf9e	ITI	0
a361a2c0-5796-11df-9093-dbf61b95cf9e	ITI	0
a3672100-5796-11df-9093-dbf61b95cf9e	ITI	0
a36b66c0-5796-11df-9093-dbf61b95cf9e	ITI	0
a36fac80-5796-11df-9093-dbf61b95cf9e	ITI	0
a3741950-5796-11df-9093-dbf61b95cf9e	ITI	0
a379e5b0-5796-11df-9093-dbf61b95cf9e	ITI	0
a37e5280-5796-11df-9093-dbf61b95cf9e	ITI	0
a3827130-5796-11df-9093-dbf61b95cf9e	ITI	0
a38864a0-5796-11df-9093-dbf61b95cf9e	ITI	0
a38dbbd0-5796-11df-9093-dbf61b95cf9e	ITI	0
a3916550-5796-11df-9093-dbf61b95cf9e	ITI	0
a3964750-5796-11df-9093-dbf61b95cf9e	ITI	0
a39b0240-5796-11df-9093-dbf61b95cf9e	ITI	0
a39ed2d0-5796-11df-9093-dbf61b95cf9e	ITI	0
a59bdb50-5796-11df-9093-dbf61b95cf9e	ITI	0
a5a1cec0-5796-11df-9093-dbf61b95cf9e	ITI	0
a5a7e940-5796-11df-9093-dbf61b95cf9e	ITI	0
a5addcb0-5796-11df-9093-dbf61b95cf9e	ITI	0
a5b3a910-5796-11df-9093-dbf61b95cf9e	ITI	0
a5b92750-5796-11df-9093-dbf61b95cf9e	ITI	0
a5be7e80-5796-11df-9093-dbf61b95cf9e	ITI	0
a5c905d0-5796-11df-9093-dbf61b95cf9e	ITI	0
a5cd99b0-5796-11df-9093-dbf61b95cf9e	ITI	0
a5d2c9d0-5796-11df-9093-dbf61b95cf9e	ITI	0
a5d6c170-5796-11df-9093-dbf61b95cf9e	ITI	0
a5dc66c0-5796-11df-9093-dbf61b95cf9e	ITI	0
a5e0ac80-5796-11df-9093-dbf61b95cf9e	ITI	0
a5e58e80-5796-11df-9093-dbf61b95cf9e	ITI	0
a5eb33d0-5796-11df-9093-dbf61b95cf9e	ITI	0
a5f10030-5796-11df-9093-dbf61b95cf9e	ITI	0
a5f59410-5796-11df-9093-dbf61b95cf9e	ITI	0
a5f9d9d0-5796-11df-9093-dbf61b95cf9e	ITI	0
a600b7a0-5796-11df-9093-dbf61b95cf9e	ITI	0
a60c0240-5796-11df-9093-dbf61b95cf9e	ITI	0
a60fd2d0-5796-11df-9093-dbf61b95cf9e	ITI	0
a613ca70-5796-11df-9093-dbf61b95cf9e	ITI	0
a618fa90-5796-11df-9093-dbf61b95cf9e	ITI	0
a61cf230-5796-11df-9093-dbf61b95cf9e	ITI	0
a620c2c0-5796-11df-9093-dbf61b95cf9e	ITI	0
a8365c50-5796-11df-9093-dbf61b95cf9e	ITI	0
a83bda90-5796-11df-9093-dbf61b95cf9e	ITI	0
a8417fe0-5796-11df-9093-dbf61b95cf9e	ITI	0
a8474c40-5796-11df-9093-dbf61b95cf9e	ITI	0
a84d66c0-5796-11df-9093-dbf61b95cf9e	ITI	0
a8533320-5796-11df-9093-dbf61b95cf9e	ITI	0
a8583c30-5796-11df-9093-dbf61b95cf9e	ITI	0
a85dba70-5796-11df-9093-dbf61b95cf9e	ITI	0
a8627560-5796-11df-9093-dbf61b95cf9e	ITI	0
a866bb20-5796-11df-9093-dbf61b95cf9e	ITI	0
a86b7610-5796-11df-9093-dbf61b95cf9e	ITI	0
a8711b60-5796-11df-9093-dbf61b95cf9e	ITI	0
a8751300-5796-11df-9093-dbf61b95cf9e	ITI	0
a87958c0-5796-11df-9093-dbf61b95cf9e	ITI	0
a87d7770-5796-11df-9093-dbf61b95cf9e	ITI	0
a882f5b0-5796-11df-9093-dbf61b95cf9e	ITI	0
a8878990-5796-11df-9093-dbf61b95cf9e	ITI	0
a88c1d70-5796-11df-9093-dbf61b95cf9e	ITI	0
a890d860-5796-11df-9093-dbf61b95cf9e	ITI	0
a8956c40-5796-11df-9093-dbf61b95cf9e	ITI	0
a898eeb0-5796-11df-9093-dbf61b95cf9e	ITI	0
a89d8290-5796-11df-9093-dbf61b95cf9e	ITI	0
a8a26490-5796-11df-9093-dbf61b95cf9e	ITI	0
a8a5e700-5796-11df-9093-dbf61b95cf9e	ITI	0
a8ac76b0-5796-11df-9093-dbf61b95cf9e	ITI	0
aa981a10-5796-11df-9093-dbf61b95cf9e	ITI	0
aa9bc390-5796-11df-9093-dbf61b95cf9e	ITI	0
aad3c380-5796-11df-9093-dbf61b95cf9e	ITI	0
aad85760-5796-11df-9093-dbf61b95cf9e	ITI	0
aae06db0-5796-11df-9093-dbf61b95cf9e	ITI	0
aae46550-5796-11df-9093-dbf61b95cf9e	ITI	0
aae96e60-5796-11df-9093-dbf61b95cf9e	ITI	0
aaeeeca0-5796-11df-9093-dbf61b95cf9e	ITI	0
aaf33260-5796-11df-9093-dbf61b95cf9e	ITI	0
aaf75110-5796-11df-9093-dbf61b95cf9e	ITI	0
aafc0c00-5796-11df-9093-dbf61b95cf9e	ITI	0
ab002ab0-5796-11df-9093-dbf61b95cf9e	ITI	0
ab044960-5796-11df-9093-dbf61b95cf9e	ITI	0
ab084100-5796-11df-9093-dbf61b95cf9e	ITI	0
ab0cfbf0-5796-11df-9093-dbf61b95cf9e	ITI	0
ab1168c0-5796-11df-9093-dbf61b95cf9e	ITI	0
ab153950-5796-11df-9093-dbf61b95cf9e	ITI	0
ab1a4260-5796-11df-9093-dbf61b95cf9e	ITI	0
ab1f2460-5796-11df-9093-dbf61b95cf9e	ITI	0
ab231c00-5796-11df-9093-dbf61b95cf9e	ITI	0
ab269e70-5796-11df-9093-dbf61b95cf9e	ITI	0
ab2a47f0-5796-11df-9093-dbf61b95cf9e	ITI	0
ab2eb4c0-5796-11df-9093-dbf61b95cf9e	ITI	0
ab328550-5796-11df-9093-dbf61b95cf9e	ITI	0
ab36a400-5796-11df-9093-dbf61b95cf9e	ITI	0
ad5bf500-5796-11df-9093-dbf61b95cf9e	ITI	0
ad636f10-5796-11df-9093-dbf61b95cf9e	ITI	0
ad685110-5796-11df-9093-dbf61b95cf9e	ITI	0
ad6d5a20-5796-11df-9093-dbf61b95cf9e	ITI	0
ad721510-5796-11df-9093-dbf61b95cf9e	ITI	0
ad78a4c0-5796-11df-9093-dbf61b95cf9e	ITI	0
ad7cc370-5796-11df-9093-dbf61b95cf9e	ITI	0
ad81cc80-5796-11df-9093-dbf61b95cf9e	ITI	0
ad8771d0-5796-11df-9093-dbf61b95cf9e	ITI	0
ad8cc900-5796-11df-9093-dbf61b95cf9e	ITI	0
ad91f920-5796-11df-9093-dbf61b95cf9e	ITI	0
ad968d00-5796-11df-9093-dbf61b95cf9e	ITI	0
ad9b20e0-5796-11df-9093-dbf61b95cf9e	ITI	0
ada13b60-5796-11df-9093-dbf61b95cf9e	ITI	0
ada61d60-5796-11df-9093-dbf61b95cf9e	ITI	0
adac5ef0-5796-11df-9093-dbf61b95cf9e	ITI	0
adb0cbc0-5796-11df-9093-dbf61b95cf9e	ITI	0
adb69820-5796-11df-9093-dbf61b95cf9e	ITI	0
adbb04f0-5796-11df-9093-dbf61b95cf9e	ITI	0
adc00e00-5796-11df-9093-dbf61b95cf9e	ITI	0
adc3de90-5796-11df-9093-dbf61b95cf9e	ITI	0
adc87270-5796-11df-9093-dbf61b95cf9e	ITI	0
adcdc9a0-5796-11df-9093-dbf61b95cf9e	ITI	0
add14c10-5796-11df-9093-dbf61b95cf9e	ITI	0
add51ca0-5796-11df-9093-dbf61b95cf9e	ITI	0
afff2890-5796-11df-9093-dbf61b95cf9e	ITI	0
b0056a20-5796-11df-9093-dbf61b95cf9e	ITI	0
b00b3680-5796-11df-9093-dbf61b95cf9e	ITI	0
b0103f90-5796-11df-9093-dbf61b95cf9e	ITI	0
b018cb10-5796-11df-9093-dbf61b95cf9e	ITI	0
b01e2240-5796-11df-9093-dbf61b95cf9e	ITI	0
b0230440-5796-11df-9093-dbf61b95cf9e	ITI	0
b028f7b0-5796-11df-9093-dbf61b95cf9e	ITI	0
b02e9d00-5796-11df-9093-dbf61b95cf9e	ITI	0
b033a610-5796-11df-9093-dbf61b95cf9e	ITI	0
b037ebd0-5796-11df-9093-dbf61b95cf9e	ITI	0
b03c0a80-5796-11df-9093-dbf61b95cf9e	ITI	0
b04188c0-5796-11df-9093-dbf61b95cf9e	ITI	0
b0461ca0-5796-11df-9093-dbf61b95cf9e	ITI	0
b04a8970-5796-11df-9093-dbf61b95cf9e	ITI	0
b04f4460-5796-11df-9093-dbf61b95cf9e	ITI	0
b055ad00-5796-11df-9093-dbf61b95cf9e	ITI	0
b05a19d0-5796-11df-9093-dbf61b95cf9e	ITI	0
b05e1170-5796-11df-9093-dbf61b95cf9e	ITI	0
b0647a10-5796-11df-9093-dbf61b95cf9e	ITI	0
b0698320-5796-11df-9093-dbf61b95cf9e	ITI	0
b06dc8e0-5796-11df-9093-dbf61b95cf9e	ITI	0
b0720ea0-5796-11df-9093-dbf61b95cf9e	ITI	0
b0762d50-5796-11df-9093-dbf61b95cf9e	ITI	0
b07b0f50-5796-11df-9093-dbf61b95cf9e	ITI	0
b283d7a0-5796-11df-9093-dbf61b95cf9e	ITI	0
b288b9a0-5796-11df-9093-dbf61b95cf9e	ITI	0
b28cff60-5796-11df-9093-dbf61b95cf9e	ITI	0
b2920870-5796-11df-9093-dbf61b95cf9e	ITI	0
b2969c50-5796-11df-9093-dbf61b95cf9e	ITI	0
b29ae210-5796-11df-9093-dbf61b95cf9e	ITI	0
b29f4ee0-5796-11df-9093-dbf61b95cf9e	ITI	0
b2a56960-5796-11df-9093-dbf61b95cf9e	ITI	0
b2aac090-5796-11df-9093-dbf61b95cf9e	ITI	0
b2af0650-5796-11df-9093-dbf61b95cf9e	ITI	0
b2b37320-5796-11df-9093-dbf61b95cf9e	ITI	0
b2b82e10-5796-11df-9093-dbf61b95cf9e	ITI	0
b2bc9ae0-5796-11df-9093-dbf61b95cf9e	ITI	0
b2c0b990-5796-11df-9093-dbf61b95cf9e	ITI	0
b2c5c2a0-5796-11df-9093-dbf61b95cf9e	ITI	0
b2ca2f70-5796-11df-9093-dbf61b95cf9e	ITI	0
b2cf3880-5796-11df-9093-dbf61b95cf9e	ITI	0
b2d3f370-5796-11df-9093-dbf61b95cf9e	ITI	0
b2d83930-5796-11df-9093-dbf61b95cf9e	ITI	0
b2dd4240-5796-11df-9093-dbf61b95cf9e	ITI	0
b2e1af10-5796-11df-9093-dbf61b95cf9e	ITI	0
b2e75460-5796-11df-9093-dbf61b95cf9e	ITI	0
b2ead6d0-5796-11df-9093-dbf61b95cf9e	ITI	0
b2ee8050-5796-11df-9093-dbf61b95cf9e	ITI	0
b2f2ed20-5796-11df-9093-dbf61b95cf9e	ITI	0
b4e9db20-5796-11df-9093-dbf61b95cf9e	ITI	0
b4efce90-5796-11df-9093-dbf61b95cf9e	ITI	0
b4f54cd0-5796-11df-9093-dbf61b95cf9e	ITI	0
b4fc9fd0-5796-11df-9093-dbf61b95cf9e	ITI	0
b503f2d0-5796-11df-9093-dbf61b95cf9e	ITI	0
b508fbe0-5796-11df-9093-dbf61b95cf9e	ITI	0
b50db6d0-5796-11df-9093-dbf61b95cf9e	ITI	0
b5161b40-5796-11df-9093-dbf61b95cf9e	ITI	0
b51aaf20-5796-11df-9093-dbf61b95cf9e	ITI	0
b51ef4e0-5796-11df-9093-dbf61b95cf9e	ITI	0
b5233aa0-5796-11df-9093-dbf61b95cf9e	ITI	0
b5295520-5796-11df-9093-dbf61b95cf9e	ITI	0
b52e3720-5796-11df-9093-dbf61b95cf9e	ITI	0
b5336740-5796-11df-9093-dbf61b95cf9e	ITI	0
b537ad00-5796-11df-9093-dbf61b95cf9e	ITI	0
b53ed8f0-5796-11df-9093-dbf61b95cf9e	ITI	0
b5436cd0-5796-11df-9093-dbf61b95cf9e	ITI	0
b5478b80-5796-11df-9093-dbf61b95cf9e	ITI	0
b54b8320-5796-11df-9093-dbf61b95cf9e	ITI	0
b550da50-5796-11df-9093-dbf61b95cf9e	ITI	0
b55483d0-5796-11df-9093-dbf61b95cf9e	ITI	0
b55965d0-5796-11df-9093-dbf61b95cf9e	ITI	0
b55d0f50-5796-11df-9093-dbf61b95cf9e	ITI	0
b5626680-5796-11df-9093-dbf61b95cf9e	ITI	0
b5672170-5796-11df-9093-dbf61b95cf9e	ITI	0
b76429f0-5796-11df-9093-dbf61b95cf9e	ITI	0
b76a9290-5796-11df-9093-dbf61b95cf9e	ITI	0
b76eb140-5796-11df-9093-dbf61b95cf9e	ITI	0
b7731e10-5796-11df-9093-dbf61b95cf9e	ITI	0
b77715b0-5796-11df-9093-dbf61b95cf9e	ITI	0
b77dcc70-5796-11df-9093-dbf61b95cf9e	ITI	0
b782d580-5796-11df-9093-dbf61b95cf9e	ITI	0
b7871b40-5796-11df-9093-dbf61b95cf9e	ITI	0
b78b8810-5796-11df-9093-dbf61b95cf9e	ITI	0
b7904300-5796-11df-9093-dbf61b95cf9e	ITI	0
b7943aa0-5796-11df-9093-dbf61b95cf9e	ITI	0
b7988060-5796-11df-9093-dbf61b95cf9e	ITI	0
b79d8970-5796-11df-9093-dbf61b95cf9e	ITI	0
b7a26b70-5796-11df-9093-dbf61b95cf9e	ITI	0
b7a79b90-5796-11df-9093-dbf61b95cf9e	ITI	0
b7b2bf20-5796-11df-9093-dbf61b95cf9e	ITI	0
b7b704e0-5796-11df-9093-dbf61b95cf9e	ITI	0
b7bc5c10-5796-11df-9093-dbf61b95cf9e	ITI	0
b7c02ca0-5796-11df-9093-dbf61b95cf9e	ITI	0
b7c3fd30-5796-11df-9093-dbf61b95cf9e	ITI	0
b7c89110-5796-11df-9093-dbf61b95cf9e	ITI	0
b7cd24f0-5796-11df-9093-dbf61b95cf9e	ITI	0
b7d0a760-5796-11df-9093-dbf61b95cf9e	ITI	0
b7d402c0-5796-11df-9093-dbf61b95cf9e	ITI	0
b7d86f90-5796-11df-9093-dbf61b95cf9e	ITI	0
ba162a90-5796-11df-9093-dbf61b95cf9e	ITI	0
ba1b5ab0-5796-11df-9093-dbf61b95cf9e	ITI	0
ba20b1e0-5796-11df-9093-dbf61b95cf9e	ITI	0
ba265730-5796-11df-9093-dbf61b95cf9e	ITI	0
ba2d8320-5796-11df-9093-dbf61b95cf9e	ITI	0
ba326520-5796-11df-9093-dbf61b95cf9e	ITI	0
ba3683d0-5796-11df-9093-dbf61b95cf9e	ITI	0
ba3b65d0-5796-11df-9093-dbf61b95cf9e	ITI	0
ba413230-5796-11df-9093-dbf61b95cf9e	ITI	0
ba4550e0-5796-11df-9093-dbf61b95cf9e	ITI	0
ba49bdb0-5796-11df-9093-dbf61b95cf9e	ITI	0
ba4db550-5796-11df-9093-dbf61b95cf9e	ITI	0
ba546c10-5796-11df-9093-dbf61b95cf9e	ITI	0
ba5ad4b0-5796-11df-9093-dbf61b95cf9e	ITI	0
ba5f1a70-5796-11df-9093-dbf61b95cf9e	ITI	0
ba642380-5796-11df-9093-dbf61b95cf9e	ITI	0
ba681b20-5796-11df-9093-dbf61b95cf9e	ITI	0
ba6e5cb0-5796-11df-9093-dbf61b95cf9e	ITI	0
ba72c980-5796-11df-9093-dbf61b95cf9e	ITI	0
ba76c120-5796-11df-9093-dbf61b95cf9e	ITI	0
ba7adfd0-5796-11df-9093-dbf61b95cf9e	ITI	0
ba7f4ca0-5796-11df-9093-dbf61b95cf9e	ITI	0
ba8455b0-5796-11df-9093-dbf61b95cf9e	ITI	0
ba87d820-5796-11df-9093-dbf61b95cf9e	ITI	0
ba8b81a0-5796-11df-9093-dbf61b95cf9e	ITI	0
\.


--
-- Data for Name: policies_usersallowed; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY policies_usersallowed (policy_key, users_allowed, "position") FROM stdin;
9c2cf5a0-5795-11df-9093-dbf61b95cf9e		0
9c433cc0-5795-11df-9093-dbf61b95cf9e		0
9c490920-5795-11df-9093-dbf61b95cf9e		0
226cf0c0-5796-11df-9093-dbf61b95cf9e	i2cat	0
22757c40-5796-11df-9093-dbf61b95cf9e	i2cat	0
227b96c0-5796-11df-9093-dbf61b95cf9e	i2cat	0
22822670-5796-11df-9093-dbf61b95cf9e	i2cat	0
228a3cc0-5796-11df-9093-dbf61b95cf9e	i2cat	0
22927a20-5796-11df-9093-dbf61b95cf9e	i2cat	0
7f250e10-5796-11df-9093-dbf61b95cf9e	i2cat-user	0
7f250e10-5796-11df-9093-dbf61b95cf9e	i2cat	1
7f2d7280-5796-11df-9093-dbf61b95cf9e	i2cat-user	0
7f2d7280-5796-11df-9093-dbf61b95cf9e	i2cat	1
7f32c9b0-5796-11df-9093-dbf61b95cf9e	i2cat-user	0
7f32c9b0-5796-11df-9093-dbf61b95cf9e	i2cat	1
7f390b40-5796-11df-9093-dbf61b95cf9e	i2cat-user	0
7f390b40-5796-11df-9093-dbf61b95cf9e	i2cat	1
7f3ed7a0-5796-11df-9093-dbf61b95cf9e	i2cat-user	0
7f3ed7a0-5796-11df-9093-dbf61b95cf9e	i2cat	1
7f4407c0-5796-11df-9093-dbf61b95cf9e	i2cat-user	0
7f4407c0-5796-11df-9093-dbf61b95cf9e	i2cat	1
9b38dc30-5796-11df-9093-dbf61b95cf9e	i2cat	0
9b3ecfa0-5796-11df-9093-dbf61b95cf9e	i2cat	0
9b475b20-5796-11df-9093-dbf61b95cf9e	i2cat	0
9b4c3d20-5796-11df-9093-dbf61b95cf9e	i2cat	0
9b516d40-5796-11df-9093-dbf61b95cf9e	i2cat	0
9b57fcf0-5796-11df-9093-dbf61b95cf9e	i2cat	0
9b5cdef0-5796-11df-9093-dbf61b95cf9e	i2cat	0
9b63bcc0-5796-11df-9093-dbf61b95cf9e	i2cat	0
9b69d740-5796-11df-9093-dbf61b95cf9e	i2cat	0
9b6e9230-5796-11df-9093-dbf61b95cf9e	i2cat	0
9b75e530-5796-11df-9093-dbf61b95cf9e	i2cat	0
9b7b8a80-5796-11df-9093-dbf61b95cf9e	i2cat	0
9b80e1b0-5796-11df-9093-dbf61b95cf9e	i2cat	0
9b850060-5796-11df-9093-dbf61b95cf9e	i2cat	0
9b8a5790-5796-11df-9093-dbf61b95cf9e	i2cat	0
9b8e9d50-5796-11df-9093-dbf61b95cf9e	i2cat	0
9b93a660-5796-11df-9093-dbf61b95cf9e	i2cat	0
9b98fd90-5796-11df-9093-dbf61b95cf9e	i2cat	0
9b9d9170-5796-11df-9093-dbf61b95cf9e	i2cat	0
9ba2e8a0-5796-11df-9093-dbf61b95cf9e	i2cat	0
9ba866e0-5796-11df-9093-dbf61b95cf9e	i2cat	0
9bac5e80-5796-11df-9093-dbf61b95cf9e	i2cat	0
9bb0cb50-5796-11df-9093-dbf61b95cf9e	i2cat	0
9bb53820-5796-11df-9093-dbf61b95cf9e	i2cat	0
9bba6840-5796-11df-9093-dbf61b95cf9e	i2cat	0
9e0d8000-5796-11df-9093-dbf61b95cf9e	i2cat	0
9e139a80-5796-11df-9093-dbf61b95cf9e	i2cat	0
9e1966e0-5796-11df-9093-dbf61b95cf9e	i2cat	0
9e1f3340-5796-11df-9093-dbf61b95cf9e	i2cat	0
9e2526b0-5796-11df-9093-dbf61b95cf9e	i2cat	0
9e2b1a20-5796-11df-9093-dbf61b95cf9e	i2cat	0
9e2f86f0-5796-11df-9093-dbf61b95cf9e	i2cat	0
9e3664c0-5796-11df-9093-dbf61b95cf9e	i2cat	0
9e3d4290-5796-11df-9093-dbf61b95cf9e	i2cat	0
9e41d670-5796-11df-9093-dbf61b95cf9e	i2cat	0
9e461c30-5796-11df-9093-dbf61b95cf9e	i2cat	0
9e4c0fa0-5796-11df-9093-dbf61b95cf9e	i2cat	0
9e50ca90-5796-11df-9093-dbf61b95cf9e	i2cat	0
9e551050-5796-11df-9093-dbf61b95cf9e	i2cat	0
9e5a1960-5796-11df-9093-dbf61b95cf9e	i2cat	0
9e605af0-5796-11df-9093-dbf61b95cf9e	i2cat	0
9e64a0b0-5796-11df-9093-dbf61b95cf9e	i2cat	0
9e6982b0-5796-11df-9093-dbf61b95cf9e	i2cat	0
9e6e8bc0-5796-11df-9093-dbf61b95cf9e	i2cat	0
9e73e2f0-5796-11df-9093-dbf61b95cf9e	i2cat	0
9e77b380-5796-11df-9093-dbf61b95cf9e	i2cat	0
9e7bd230-5796-11df-9093-dbf61b95cf9e	i2cat	0
9e7fc9d0-5796-11df-9093-dbf61b95cf9e	i2cat	0
9e865980-5796-11df-9093-dbf61b95cf9e	i2cat	0
9e8a2a10-5796-11df-9093-dbf61b95cf9e	i2cat	0
a0aeded0-5796-11df-9093-dbf61b95cf9e	i2cat	0
a0b4f950-5796-11df-9093-dbf61b95cf9e	i2cat	0
a0ba0260-5796-11df-9093-dbf61b95cf9e	i2cat	0
a0c06b00-5796-11df-9093-dbf61b95cf9e	i2cat	0
a0c7be00-5796-11df-9093-dbf61b95cf9e	i2cat	0
a0ce26a0-5796-11df-9093-dbf61b95cf9e	i2cat	0
a0d3cbf0-5796-11df-9093-dbf61b95cf9e	i2cat	0
a0d99850-5796-11df-9093-dbf61b95cf9e	i2cat	0
a0dec870-5796-11df-9093-dbf61b95cf9e	i2cat	0
a0e4bbe0-5796-11df-9093-dbf61b95cf9e	i2cat	0
a0ea1310-5796-11df-9093-dbf61b95cf9e	i2cat	0
a0eea6f0-5796-11df-9093-dbf61b95cf9e	i2cat	0
a0f33ad0-5796-11df-9093-dbf61b95cf9e	i2cat	0
a0f7f5c0-5796-11df-9093-dbf61b95cf9e	i2cat	0
a0fdc220-5796-11df-9093-dbf61b95cf9e	i2cat	0
a1025600-5796-11df-9093-dbf61b95cf9e	i2cat	0
a107ad30-5796-11df-9093-dbf61b95cf9e	i2cat	0
a10d2b70-5796-11df-9093-dbf61b95cf9e	i2cat	0
a1123480-5796-11df-9093-dbf61b95cf9e	i2cat	0
a1178bb0-5796-11df-9093-dbf61b95cf9e	i2cat	0
a11b3530-5796-11df-9093-dbf61b95cf9e	i2cat	0
a120da80-5796-11df-9093-dbf61b95cf9e	i2cat	0
a1248400-5796-11df-9093-dbf61b95cf9e	i2cat	0
a1282d80-5796-11df-9093-dbf61b95cf9e	i2cat	0
a12c9a50-5796-11df-9093-dbf61b95cf9e	i2cat	0
a327f520-5796-11df-9093-dbf61b95cf9e	i2cat	0
a32fbd50-5796-11df-9093-dbf61b95cf9e	i2cat	0
a3347840-5796-11df-9093-dbf61b95cf9e	i2cat	0
a339a860-5796-11df-9093-dbf61b95cf9e	i2cat	0
a33e1530-5796-11df-9093-dbf61b95cf9e	i2cat	0
a34408a0-5796-11df-9093-dbf61b95cf9e	i2cat	0
a3484e60-5796-11df-9093-dbf61b95cf9e	i2cat	0
a34da590-5796-11df-9093-dbf61b95cf9e	i2cat	0
a3528790-5796-11df-9093-dbf61b95cf9e	i2cat	0
a357dec0-5796-11df-9093-dbf61b95cf9e	i2cat	0
a35cc0c0-5796-11df-9093-dbf61b95cf9e	i2cat	0
a361a2c0-5796-11df-9093-dbf61b95cf9e	i2cat	0
a3672100-5796-11df-9093-dbf61b95cf9e	i2cat	0
a36b66c0-5796-11df-9093-dbf61b95cf9e	i2cat	0
a36fac80-5796-11df-9093-dbf61b95cf9e	i2cat	0
a3741950-5796-11df-9093-dbf61b95cf9e	i2cat	0
a379e5b0-5796-11df-9093-dbf61b95cf9e	i2cat	0
a37e5280-5796-11df-9093-dbf61b95cf9e	i2cat	0
a3827130-5796-11df-9093-dbf61b95cf9e	i2cat	0
a38864a0-5796-11df-9093-dbf61b95cf9e	i2cat	0
a38dbbd0-5796-11df-9093-dbf61b95cf9e	i2cat	0
a3916550-5796-11df-9093-dbf61b95cf9e	i2cat	0
a3964750-5796-11df-9093-dbf61b95cf9e	i2cat	0
a39b0240-5796-11df-9093-dbf61b95cf9e	i2cat	0
a39ed2d0-5796-11df-9093-dbf61b95cf9e	i2cat	0
a59bdb50-5796-11df-9093-dbf61b95cf9e	i2cat	0
a5a1cec0-5796-11df-9093-dbf61b95cf9e	i2cat	0
a5a7e940-5796-11df-9093-dbf61b95cf9e	i2cat	0
a5addcb0-5796-11df-9093-dbf61b95cf9e	i2cat	0
a5b3a910-5796-11df-9093-dbf61b95cf9e	i2cat	0
a5b92750-5796-11df-9093-dbf61b95cf9e	i2cat	0
a5be7e80-5796-11df-9093-dbf61b95cf9e	i2cat	0
a5c905d0-5796-11df-9093-dbf61b95cf9e	i2cat	0
a5cd99b0-5796-11df-9093-dbf61b95cf9e	i2cat	0
a5d2c9d0-5796-11df-9093-dbf61b95cf9e	i2cat	0
a5d6c170-5796-11df-9093-dbf61b95cf9e	i2cat	0
a5dc66c0-5796-11df-9093-dbf61b95cf9e	i2cat	0
a5e0ac80-5796-11df-9093-dbf61b95cf9e	i2cat	0
a5e58e80-5796-11df-9093-dbf61b95cf9e	i2cat	0
a5eb33d0-5796-11df-9093-dbf61b95cf9e	i2cat	0
a5f10030-5796-11df-9093-dbf61b95cf9e	i2cat	0
a5f59410-5796-11df-9093-dbf61b95cf9e	i2cat	0
a5f9d9d0-5796-11df-9093-dbf61b95cf9e	i2cat	0
a600b7a0-5796-11df-9093-dbf61b95cf9e	i2cat	0
a60c0240-5796-11df-9093-dbf61b95cf9e	i2cat	0
a60fd2d0-5796-11df-9093-dbf61b95cf9e	i2cat	0
a613ca70-5796-11df-9093-dbf61b95cf9e	i2cat	0
a618fa90-5796-11df-9093-dbf61b95cf9e	i2cat	0
a61cf230-5796-11df-9093-dbf61b95cf9e	i2cat	0
a620c2c0-5796-11df-9093-dbf61b95cf9e	i2cat	0
a8365c50-5796-11df-9093-dbf61b95cf9e	i2cat	0
a83bda90-5796-11df-9093-dbf61b95cf9e	i2cat	0
a8417fe0-5796-11df-9093-dbf61b95cf9e	i2cat	0
a8474c40-5796-11df-9093-dbf61b95cf9e	i2cat	0
a84d66c0-5796-11df-9093-dbf61b95cf9e	i2cat	0
a8533320-5796-11df-9093-dbf61b95cf9e	i2cat	0
a8583c30-5796-11df-9093-dbf61b95cf9e	i2cat	0
a85dba70-5796-11df-9093-dbf61b95cf9e	i2cat	0
a8627560-5796-11df-9093-dbf61b95cf9e	i2cat	0
a866bb20-5796-11df-9093-dbf61b95cf9e	i2cat	0
a86b7610-5796-11df-9093-dbf61b95cf9e	i2cat	0
a8711b60-5796-11df-9093-dbf61b95cf9e	i2cat	0
a8751300-5796-11df-9093-dbf61b95cf9e	i2cat	0
a87958c0-5796-11df-9093-dbf61b95cf9e	i2cat	0
a87d7770-5796-11df-9093-dbf61b95cf9e	i2cat	0
a882f5b0-5796-11df-9093-dbf61b95cf9e	i2cat	0
a8878990-5796-11df-9093-dbf61b95cf9e	i2cat	0
a88c1d70-5796-11df-9093-dbf61b95cf9e	i2cat	0
a890d860-5796-11df-9093-dbf61b95cf9e	i2cat	0
a8956c40-5796-11df-9093-dbf61b95cf9e	i2cat	0
a898eeb0-5796-11df-9093-dbf61b95cf9e	i2cat	0
a89d8290-5796-11df-9093-dbf61b95cf9e	i2cat	0
a8a26490-5796-11df-9093-dbf61b95cf9e	i2cat	0
a8a5e700-5796-11df-9093-dbf61b95cf9e	i2cat	0
a8ac76b0-5796-11df-9093-dbf61b95cf9e	i2cat	0
aa981a10-5796-11df-9093-dbf61b95cf9e	i2cat	0
aa9bc390-5796-11df-9093-dbf61b95cf9e	i2cat	0
aad3c380-5796-11df-9093-dbf61b95cf9e	i2cat	0
aad85760-5796-11df-9093-dbf61b95cf9e	i2cat	0
aae06db0-5796-11df-9093-dbf61b95cf9e	i2cat	0
aae46550-5796-11df-9093-dbf61b95cf9e	i2cat	0
aae96e60-5796-11df-9093-dbf61b95cf9e	i2cat	0
aaeeeca0-5796-11df-9093-dbf61b95cf9e	i2cat	0
aaf33260-5796-11df-9093-dbf61b95cf9e	i2cat	0
aaf75110-5796-11df-9093-dbf61b95cf9e	i2cat	0
aafc0c00-5796-11df-9093-dbf61b95cf9e	i2cat	0
ab002ab0-5796-11df-9093-dbf61b95cf9e	i2cat	0
ab044960-5796-11df-9093-dbf61b95cf9e	i2cat	0
ab084100-5796-11df-9093-dbf61b95cf9e	i2cat	0
ab0cfbf0-5796-11df-9093-dbf61b95cf9e	i2cat	0
ab1168c0-5796-11df-9093-dbf61b95cf9e	i2cat	0
ab153950-5796-11df-9093-dbf61b95cf9e	i2cat	0
ab1a4260-5796-11df-9093-dbf61b95cf9e	i2cat	0
ab1f2460-5796-11df-9093-dbf61b95cf9e	i2cat	0
ab231c00-5796-11df-9093-dbf61b95cf9e	i2cat	0
ab269e70-5796-11df-9093-dbf61b95cf9e	i2cat	0
ab2a47f0-5796-11df-9093-dbf61b95cf9e	i2cat	0
ab2eb4c0-5796-11df-9093-dbf61b95cf9e	i2cat	0
ab328550-5796-11df-9093-dbf61b95cf9e	i2cat	0
ab36a400-5796-11df-9093-dbf61b95cf9e	i2cat	0
ad5bf500-5796-11df-9093-dbf61b95cf9e	i2cat	0
ad636f10-5796-11df-9093-dbf61b95cf9e	i2cat	0
ad685110-5796-11df-9093-dbf61b95cf9e	i2cat	0
ad6d5a20-5796-11df-9093-dbf61b95cf9e	i2cat	0
ad721510-5796-11df-9093-dbf61b95cf9e	i2cat	0
ad78a4c0-5796-11df-9093-dbf61b95cf9e	i2cat	0
ad7cc370-5796-11df-9093-dbf61b95cf9e	i2cat	0
ad81cc80-5796-11df-9093-dbf61b95cf9e	i2cat	0
ad8771d0-5796-11df-9093-dbf61b95cf9e	i2cat	0
ad8cc900-5796-11df-9093-dbf61b95cf9e	i2cat	0
ad91f920-5796-11df-9093-dbf61b95cf9e	i2cat	0
ad968d00-5796-11df-9093-dbf61b95cf9e	i2cat	0
ad9b20e0-5796-11df-9093-dbf61b95cf9e	i2cat	0
ada13b60-5796-11df-9093-dbf61b95cf9e	i2cat	0
ada61d60-5796-11df-9093-dbf61b95cf9e	i2cat	0
adac5ef0-5796-11df-9093-dbf61b95cf9e	i2cat	0
adb0cbc0-5796-11df-9093-dbf61b95cf9e	i2cat	0
adb69820-5796-11df-9093-dbf61b95cf9e	i2cat	0
adbb04f0-5796-11df-9093-dbf61b95cf9e	i2cat	0
adc00e00-5796-11df-9093-dbf61b95cf9e	i2cat	0
adc3de90-5796-11df-9093-dbf61b95cf9e	i2cat	0
adc87270-5796-11df-9093-dbf61b95cf9e	i2cat	0
adcdc9a0-5796-11df-9093-dbf61b95cf9e	i2cat	0
add14c10-5796-11df-9093-dbf61b95cf9e	i2cat	0
add51ca0-5796-11df-9093-dbf61b95cf9e	i2cat	0
afff2890-5796-11df-9093-dbf61b95cf9e	i2cat	0
b0056a20-5796-11df-9093-dbf61b95cf9e	i2cat	0
b00b3680-5796-11df-9093-dbf61b95cf9e	i2cat	0
b0103f90-5796-11df-9093-dbf61b95cf9e	i2cat	0
b018cb10-5796-11df-9093-dbf61b95cf9e	i2cat	0
b01e2240-5796-11df-9093-dbf61b95cf9e	i2cat	0
b0230440-5796-11df-9093-dbf61b95cf9e	i2cat	0
b028f7b0-5796-11df-9093-dbf61b95cf9e	i2cat	0
b02e9d00-5796-11df-9093-dbf61b95cf9e	i2cat	0
b033a610-5796-11df-9093-dbf61b95cf9e	i2cat	0
b037ebd0-5796-11df-9093-dbf61b95cf9e	i2cat	0
b03c0a80-5796-11df-9093-dbf61b95cf9e	i2cat	0
b04188c0-5796-11df-9093-dbf61b95cf9e	i2cat	0
b0461ca0-5796-11df-9093-dbf61b95cf9e	i2cat	0
b04a8970-5796-11df-9093-dbf61b95cf9e	i2cat	0
b04f4460-5796-11df-9093-dbf61b95cf9e	i2cat	0
b055ad00-5796-11df-9093-dbf61b95cf9e	i2cat	0
b05a19d0-5796-11df-9093-dbf61b95cf9e	i2cat	0
b05e1170-5796-11df-9093-dbf61b95cf9e	i2cat	0
b0647a10-5796-11df-9093-dbf61b95cf9e	i2cat	0
b0698320-5796-11df-9093-dbf61b95cf9e	i2cat	0
b06dc8e0-5796-11df-9093-dbf61b95cf9e	i2cat	0
b0720ea0-5796-11df-9093-dbf61b95cf9e	i2cat	0
b0762d50-5796-11df-9093-dbf61b95cf9e	i2cat	0
b07b0f50-5796-11df-9093-dbf61b95cf9e	i2cat	0
b283d7a0-5796-11df-9093-dbf61b95cf9e	i2cat	0
b288b9a0-5796-11df-9093-dbf61b95cf9e	i2cat	0
b28cff60-5796-11df-9093-dbf61b95cf9e	i2cat	0
b2920870-5796-11df-9093-dbf61b95cf9e	i2cat	0
b2969c50-5796-11df-9093-dbf61b95cf9e	i2cat	0
b29ae210-5796-11df-9093-dbf61b95cf9e	i2cat	0
b29f4ee0-5796-11df-9093-dbf61b95cf9e	i2cat	0
b2a56960-5796-11df-9093-dbf61b95cf9e	i2cat	0
b2aac090-5796-11df-9093-dbf61b95cf9e	i2cat	0
b2af0650-5796-11df-9093-dbf61b95cf9e	i2cat	0
b2b37320-5796-11df-9093-dbf61b95cf9e	i2cat	0
b2b82e10-5796-11df-9093-dbf61b95cf9e	i2cat	0
b2bc9ae0-5796-11df-9093-dbf61b95cf9e	i2cat	0
b2c0b990-5796-11df-9093-dbf61b95cf9e	i2cat	0
b2c5c2a0-5796-11df-9093-dbf61b95cf9e	i2cat	0
b2ca2f70-5796-11df-9093-dbf61b95cf9e	i2cat	0
b2cf3880-5796-11df-9093-dbf61b95cf9e	i2cat	0
b2d3f370-5796-11df-9093-dbf61b95cf9e	i2cat	0
b2d83930-5796-11df-9093-dbf61b95cf9e	i2cat	0
b2dd4240-5796-11df-9093-dbf61b95cf9e	i2cat	0
b2e1af10-5796-11df-9093-dbf61b95cf9e	i2cat	0
b2e75460-5796-11df-9093-dbf61b95cf9e	i2cat	0
b2ead6d0-5796-11df-9093-dbf61b95cf9e	i2cat	0
b2ee8050-5796-11df-9093-dbf61b95cf9e	i2cat	0
b2f2ed20-5796-11df-9093-dbf61b95cf9e	i2cat	0
b4e9db20-5796-11df-9093-dbf61b95cf9e	i2cat	0
b4efce90-5796-11df-9093-dbf61b95cf9e	i2cat	0
b4f54cd0-5796-11df-9093-dbf61b95cf9e	i2cat	0
b4fc9fd0-5796-11df-9093-dbf61b95cf9e	i2cat	0
b503f2d0-5796-11df-9093-dbf61b95cf9e	i2cat	0
b508fbe0-5796-11df-9093-dbf61b95cf9e	i2cat	0
b50db6d0-5796-11df-9093-dbf61b95cf9e	i2cat	0
b5161b40-5796-11df-9093-dbf61b95cf9e	i2cat	0
b51aaf20-5796-11df-9093-dbf61b95cf9e	i2cat	0
b51ef4e0-5796-11df-9093-dbf61b95cf9e	i2cat	0
b5233aa0-5796-11df-9093-dbf61b95cf9e	i2cat	0
b5295520-5796-11df-9093-dbf61b95cf9e	i2cat	0
b52e3720-5796-11df-9093-dbf61b95cf9e	i2cat	0
b5336740-5796-11df-9093-dbf61b95cf9e	i2cat	0
b537ad00-5796-11df-9093-dbf61b95cf9e	i2cat	0
b53ed8f0-5796-11df-9093-dbf61b95cf9e	i2cat	0
b5436cd0-5796-11df-9093-dbf61b95cf9e	i2cat	0
b5478b80-5796-11df-9093-dbf61b95cf9e	i2cat	0
b54b8320-5796-11df-9093-dbf61b95cf9e	i2cat	0
b550da50-5796-11df-9093-dbf61b95cf9e	i2cat	0
b55483d0-5796-11df-9093-dbf61b95cf9e	i2cat	0
b55965d0-5796-11df-9093-dbf61b95cf9e	i2cat	0
b55d0f50-5796-11df-9093-dbf61b95cf9e	i2cat	0
b5626680-5796-11df-9093-dbf61b95cf9e	i2cat	0
b5672170-5796-11df-9093-dbf61b95cf9e	i2cat	0
b76429f0-5796-11df-9093-dbf61b95cf9e	i2cat	0
b76a9290-5796-11df-9093-dbf61b95cf9e	i2cat	0
b76eb140-5796-11df-9093-dbf61b95cf9e	i2cat	0
b7731e10-5796-11df-9093-dbf61b95cf9e	i2cat	0
b77715b0-5796-11df-9093-dbf61b95cf9e	i2cat	0
b77dcc70-5796-11df-9093-dbf61b95cf9e	i2cat	0
b782d580-5796-11df-9093-dbf61b95cf9e	i2cat	0
b7871b40-5796-11df-9093-dbf61b95cf9e	i2cat	0
b78b8810-5796-11df-9093-dbf61b95cf9e	i2cat	0
b7904300-5796-11df-9093-dbf61b95cf9e	i2cat	0
b7943aa0-5796-11df-9093-dbf61b95cf9e	i2cat	0
b7988060-5796-11df-9093-dbf61b95cf9e	i2cat	0
b79d8970-5796-11df-9093-dbf61b95cf9e	i2cat	0
b7a26b70-5796-11df-9093-dbf61b95cf9e	i2cat	0
b7a79b90-5796-11df-9093-dbf61b95cf9e	i2cat	0
b7b2bf20-5796-11df-9093-dbf61b95cf9e	i2cat	0
b7b704e0-5796-11df-9093-dbf61b95cf9e	i2cat	0
b7bc5c10-5796-11df-9093-dbf61b95cf9e	i2cat	0
b7c02ca0-5796-11df-9093-dbf61b95cf9e	i2cat	0
b7c3fd30-5796-11df-9093-dbf61b95cf9e	i2cat	0
b7c89110-5796-11df-9093-dbf61b95cf9e	i2cat	0
b7cd24f0-5796-11df-9093-dbf61b95cf9e	i2cat	0
b7d0a760-5796-11df-9093-dbf61b95cf9e	i2cat	0
b7d402c0-5796-11df-9093-dbf61b95cf9e	i2cat	0
b7d86f90-5796-11df-9093-dbf61b95cf9e	i2cat	0
ba162a90-5796-11df-9093-dbf61b95cf9e	i2cat	0
ba1b5ab0-5796-11df-9093-dbf61b95cf9e	i2cat	0
ba20b1e0-5796-11df-9093-dbf61b95cf9e	i2cat	0
ba265730-5796-11df-9093-dbf61b95cf9e	i2cat	0
ba2d8320-5796-11df-9093-dbf61b95cf9e	i2cat	0
ba326520-5796-11df-9093-dbf61b95cf9e	i2cat	0
ba3683d0-5796-11df-9093-dbf61b95cf9e	i2cat	0
ba3b65d0-5796-11df-9093-dbf61b95cf9e	i2cat	0
ba413230-5796-11df-9093-dbf61b95cf9e	i2cat	0
ba4550e0-5796-11df-9093-dbf61b95cf9e	i2cat	0
ba49bdb0-5796-11df-9093-dbf61b95cf9e	i2cat	0
ba4db550-5796-11df-9093-dbf61b95cf9e	i2cat	0
ba546c10-5796-11df-9093-dbf61b95cf9e	i2cat	0
ba5ad4b0-5796-11df-9093-dbf61b95cf9e	i2cat	0
ba5f1a70-5796-11df-9093-dbf61b95cf9e	i2cat	0
ba642380-5796-11df-9093-dbf61b95cf9e	i2cat	0
ba681b20-5796-11df-9093-dbf61b95cf9e	i2cat	0
ba6e5cb0-5796-11df-9093-dbf61b95cf9e	i2cat	0
ba72c980-5796-11df-9093-dbf61b95cf9e	i2cat	0
ba76c120-5796-11df-9093-dbf61b95cf9e	i2cat	0
ba7adfd0-5796-11df-9093-dbf61b95cf9e	i2cat	0
ba7f4ca0-5796-11df-9093-dbf61b95cf9e	i2cat	0
ba8455b0-5796-11df-9093-dbf61b95cf9e	i2cat	0
ba87d820-5796-11df-9093-dbf61b95cf9e	i2cat	0
ba8b81a0-5796-11df-9093-dbf61b95cf9e	i2cat	0
\.


--
-- Data for Name: policy; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY policy (policy_key, operation, resource_key) FROM stdin;
9c2cf5a0-5795-11df-9093-dbf61b95cf9e	create:userManagement	
9c433cc0-5795-11df-9093-dbf61b95cf9e	create:router	
9c490920-5795-11df-9093-dbf61b95cf9e	create:ipnetwork	
226cf0c0-5796-11df-9093-dbf61b95cf9e	modify:userManagement	20c31150-5796-11df-9093-dbf61b95cf9e
22757c40-5796-11df-9093-dbf61b95cf9e	destroy:userManagement	20c31150-5796-11df-9093-dbf61b95cf9e
227b96c0-5796-11df-9093-dbf61b95cf9e	find:userManagement	20c31150-5796-11df-9093-dbf61b95cf9e
22822670-5796-11df-9093-dbf61b95cf9e	getUsers:userManagement	20c31150-5796-11df-9093-dbf61b95cf9e
228a3cc0-5796-11df-9093-dbf61b95cf9e	getMultipleResourceProperties:userManagement	20c31150-5796-11df-9093-dbf61b95cf9e
22927a20-5796-11df-9093-dbf61b95cf9e	setPolicies:policies	20c31150-5796-11df-9093-dbf61b95cf9e
7f250e10-5796-11df-9093-dbf61b95cf9e	modify:userManagement	7e04a9f0-5796-11df-9093-dbf61b95cf9e
7f2d7280-5796-11df-9093-dbf61b95cf9e	destroy:userManagement	7e04a9f0-5796-11df-9093-dbf61b95cf9e
7f32c9b0-5796-11df-9093-dbf61b95cf9e	find:userManagement	7e04a9f0-5796-11df-9093-dbf61b95cf9e
7f390b40-5796-11df-9093-dbf61b95cf9e	getUsers:userManagement	7e04a9f0-5796-11df-9093-dbf61b95cf9e
7f3ed7a0-5796-11df-9093-dbf61b95cf9e	getMultipleResourceProperties:userManagement	7e04a9f0-5796-11df-9093-dbf61b95cf9e
7f4407c0-5796-11df-9093-dbf61b95cf9e	setPolicies:policies	7e04a9f0-5796-11df-9093-dbf61b95cf9e
9b38dc30-5796-11df-9093-dbf61b95cf9e	createNewRouterInstance:router	9856a3d0-5796-11df-9093-dbf61b95cf9e
9b3ecfa0-5796-11df-9093-dbf61b95cf9e	setPollingPeriod:router	9856a3d0-5796-11df-9093-dbf61b95cf9e
9b475b20-5796-11df-9093-dbf61b95cf9e	invoke:router	9856a3d0-5796-11df-9093-dbf61b95cf9e
9b4c3d20-5796-11df-9093-dbf61b95cf9e	invoke:router	9856a3d0-5796-11df-9093-dbf61b95cf9e
9b516d40-5796-11df-9093-dbf61b95cf9e	invoke:router	9856a3d0-5796-11df-9093-dbf61b95cf9e
9b57fcf0-5796-11df-9093-dbf61b95cf9e	invoke:router	9856a3d0-5796-11df-9093-dbf61b95cf9e
9b5cdef0-5796-11df-9093-dbf61b95cf9e	invoke:router	9856a3d0-5796-11df-9093-dbf61b95cf9e
9b63bcc0-5796-11df-9093-dbf61b95cf9e	invoke:router	9856a3d0-5796-11df-9093-dbf61b95cf9e
9b69d740-5796-11df-9093-dbf61b95cf9e	invoke:router	9856a3d0-5796-11df-9093-dbf61b95cf9e
9b6e9230-5796-11df-9093-dbf61b95cf9e	invoke:router	9856a3d0-5796-11df-9093-dbf61b95cf9e
9b75e530-5796-11df-9093-dbf61b95cf9e	invoke:router	9856a3d0-5796-11df-9093-dbf61b95cf9e
9b7b8a80-5796-11df-9093-dbf61b95cf9e	invoke:router	9856a3d0-5796-11df-9093-dbf61b95cf9e
9b80e1b0-5796-11df-9093-dbf61b95cf9e	invoke:router	9856a3d0-5796-11df-9093-dbf61b95cf9e
9b850060-5796-11df-9093-dbf61b95cf9e	invoke:router	9856a3d0-5796-11df-9093-dbf61b95cf9e
9b8a5790-5796-11df-9093-dbf61b95cf9e	invoke:router	9856a3d0-5796-11df-9093-dbf61b95cf9e
9b8e9d50-5796-11df-9093-dbf61b95cf9e	invoke:router	9856a3d0-5796-11df-9093-dbf61b95cf9e
9b93a660-5796-11df-9093-dbf61b95cf9e	invoke:router	9856a3d0-5796-11df-9093-dbf61b95cf9e
9b98fd90-5796-11df-9093-dbf61b95cf9e	invoke:router	9856a3d0-5796-11df-9093-dbf61b95cf9e
9b9d9170-5796-11df-9093-dbf61b95cf9e	invoke:router	9856a3d0-5796-11df-9093-dbf61b95cf9e
9ba2e8a0-5796-11df-9093-dbf61b95cf9e	invoke:router	9856a3d0-5796-11df-9093-dbf61b95cf9e
9ba866e0-5796-11df-9093-dbf61b95cf9e	destroy:router	9856a3d0-5796-11df-9093-dbf61b95cf9e
9bac5e80-5796-11df-9093-dbf61b95cf9e	getMultipleResourceProperties:router	9856a3d0-5796-11df-9093-dbf61b95cf9e
9bb0cb50-5796-11df-9093-dbf61b95cf9e	modify:router	9856a3d0-5796-11df-9093-dbf61b95cf9e
9bb53820-5796-11df-9093-dbf61b95cf9e	getRouters:router	9856a3d0-5796-11df-9093-dbf61b95cf9e
9bba6840-5796-11df-9093-dbf61b95cf9e	setPolicies:policies	9856a3d0-5796-11df-9093-dbf61b95cf9e
9e0d8000-5796-11df-9093-dbf61b95cf9e	createNewRouterInstance:router	9bde6b00-5796-11df-9093-dbf61b95cf9e
9e139a80-5796-11df-9093-dbf61b95cf9e	setPollingPeriod:router	9bde6b00-5796-11df-9093-dbf61b95cf9e
9e1966e0-5796-11df-9093-dbf61b95cf9e	invoke:router	9bde6b00-5796-11df-9093-dbf61b95cf9e
9e1f3340-5796-11df-9093-dbf61b95cf9e	invoke:router	9bde6b00-5796-11df-9093-dbf61b95cf9e
9e2526b0-5796-11df-9093-dbf61b95cf9e	invoke:router	9bde6b00-5796-11df-9093-dbf61b95cf9e
9e2b1a20-5796-11df-9093-dbf61b95cf9e	invoke:router	9bde6b00-5796-11df-9093-dbf61b95cf9e
9e2f86f0-5796-11df-9093-dbf61b95cf9e	invoke:router	9bde6b00-5796-11df-9093-dbf61b95cf9e
9e3664c0-5796-11df-9093-dbf61b95cf9e	invoke:router	9bde6b00-5796-11df-9093-dbf61b95cf9e
9e3d4290-5796-11df-9093-dbf61b95cf9e	invoke:router	9bde6b00-5796-11df-9093-dbf61b95cf9e
9e41d670-5796-11df-9093-dbf61b95cf9e	invoke:router	9bde6b00-5796-11df-9093-dbf61b95cf9e
9e461c30-5796-11df-9093-dbf61b95cf9e	invoke:router	9bde6b00-5796-11df-9093-dbf61b95cf9e
9e4c0fa0-5796-11df-9093-dbf61b95cf9e	invoke:router	9bde6b00-5796-11df-9093-dbf61b95cf9e
9e50ca90-5796-11df-9093-dbf61b95cf9e	invoke:router	9bde6b00-5796-11df-9093-dbf61b95cf9e
9e551050-5796-11df-9093-dbf61b95cf9e	invoke:router	9bde6b00-5796-11df-9093-dbf61b95cf9e
9e5a1960-5796-11df-9093-dbf61b95cf9e	invoke:router	9bde6b00-5796-11df-9093-dbf61b95cf9e
9e605af0-5796-11df-9093-dbf61b95cf9e	invoke:router	9bde6b00-5796-11df-9093-dbf61b95cf9e
9e64a0b0-5796-11df-9093-dbf61b95cf9e	invoke:router	9bde6b00-5796-11df-9093-dbf61b95cf9e
9e6982b0-5796-11df-9093-dbf61b95cf9e	invoke:router	9bde6b00-5796-11df-9093-dbf61b95cf9e
9e6e8bc0-5796-11df-9093-dbf61b95cf9e	invoke:router	9bde6b00-5796-11df-9093-dbf61b95cf9e
9e73e2f0-5796-11df-9093-dbf61b95cf9e	invoke:router	9bde6b00-5796-11df-9093-dbf61b95cf9e
9e77b380-5796-11df-9093-dbf61b95cf9e	destroy:router	9bde6b00-5796-11df-9093-dbf61b95cf9e
9e7bd230-5796-11df-9093-dbf61b95cf9e	getMultipleResourceProperties:router	9bde6b00-5796-11df-9093-dbf61b95cf9e
9e7fc9d0-5796-11df-9093-dbf61b95cf9e	modify:router	9bde6b00-5796-11df-9093-dbf61b95cf9e
9e865980-5796-11df-9093-dbf61b95cf9e	getRouters:router	9bde6b00-5796-11df-9093-dbf61b95cf9e
9e8a2a10-5796-11df-9093-dbf61b95cf9e	setPolicies:policies	9bde6b00-5796-11df-9093-dbf61b95cf9e
a0aeded0-5796-11df-9093-dbf61b95cf9e	createNewRouterInstance:router	9eaca630-5796-11df-9093-dbf61b95cf9e
a0b4f950-5796-11df-9093-dbf61b95cf9e	setPollingPeriod:router	9eaca630-5796-11df-9093-dbf61b95cf9e
a0ba0260-5796-11df-9093-dbf61b95cf9e	invoke:router	9eaca630-5796-11df-9093-dbf61b95cf9e
a0c06b00-5796-11df-9093-dbf61b95cf9e	invoke:router	9eaca630-5796-11df-9093-dbf61b95cf9e
a0c7be00-5796-11df-9093-dbf61b95cf9e	invoke:router	9eaca630-5796-11df-9093-dbf61b95cf9e
a0ce26a0-5796-11df-9093-dbf61b95cf9e	invoke:router	9eaca630-5796-11df-9093-dbf61b95cf9e
a0d3cbf0-5796-11df-9093-dbf61b95cf9e	invoke:router	9eaca630-5796-11df-9093-dbf61b95cf9e
a0d99850-5796-11df-9093-dbf61b95cf9e	invoke:router	9eaca630-5796-11df-9093-dbf61b95cf9e
a0dec870-5796-11df-9093-dbf61b95cf9e	invoke:router	9eaca630-5796-11df-9093-dbf61b95cf9e
a0e4bbe0-5796-11df-9093-dbf61b95cf9e	invoke:router	9eaca630-5796-11df-9093-dbf61b95cf9e
a0ea1310-5796-11df-9093-dbf61b95cf9e	invoke:router	9eaca630-5796-11df-9093-dbf61b95cf9e
a0eea6f0-5796-11df-9093-dbf61b95cf9e	invoke:router	9eaca630-5796-11df-9093-dbf61b95cf9e
a0f33ad0-5796-11df-9093-dbf61b95cf9e	invoke:router	9eaca630-5796-11df-9093-dbf61b95cf9e
a0f7f5c0-5796-11df-9093-dbf61b95cf9e	invoke:router	9eaca630-5796-11df-9093-dbf61b95cf9e
a0fdc220-5796-11df-9093-dbf61b95cf9e	invoke:router	9eaca630-5796-11df-9093-dbf61b95cf9e
a1025600-5796-11df-9093-dbf61b95cf9e	invoke:router	9eaca630-5796-11df-9093-dbf61b95cf9e
a107ad30-5796-11df-9093-dbf61b95cf9e	invoke:router	9eaca630-5796-11df-9093-dbf61b95cf9e
a10d2b70-5796-11df-9093-dbf61b95cf9e	invoke:router	9eaca630-5796-11df-9093-dbf61b95cf9e
a1123480-5796-11df-9093-dbf61b95cf9e	invoke:router	9eaca630-5796-11df-9093-dbf61b95cf9e
a1178bb0-5796-11df-9093-dbf61b95cf9e	invoke:router	9eaca630-5796-11df-9093-dbf61b95cf9e
a11b3530-5796-11df-9093-dbf61b95cf9e	destroy:router	9eaca630-5796-11df-9093-dbf61b95cf9e
a120da80-5796-11df-9093-dbf61b95cf9e	getMultipleResourceProperties:router	9eaca630-5796-11df-9093-dbf61b95cf9e
a1248400-5796-11df-9093-dbf61b95cf9e	modify:router	9eaca630-5796-11df-9093-dbf61b95cf9e
a1282d80-5796-11df-9093-dbf61b95cf9e	getRouters:router	9eaca630-5796-11df-9093-dbf61b95cf9e
a12c9a50-5796-11df-9093-dbf61b95cf9e	setPolicies:policies	9eaca630-5796-11df-9093-dbf61b95cf9e
a327f520-5796-11df-9093-dbf61b95cf9e	createNewRouterInstance:router	a13fd430-5796-11df-9093-dbf61b95cf9e
a32fbd50-5796-11df-9093-dbf61b95cf9e	setPollingPeriod:router	a13fd430-5796-11df-9093-dbf61b95cf9e
a3347840-5796-11df-9093-dbf61b95cf9e	invoke:router	a13fd430-5796-11df-9093-dbf61b95cf9e
a339a860-5796-11df-9093-dbf61b95cf9e	invoke:router	a13fd430-5796-11df-9093-dbf61b95cf9e
a33e1530-5796-11df-9093-dbf61b95cf9e	invoke:router	a13fd430-5796-11df-9093-dbf61b95cf9e
a34408a0-5796-11df-9093-dbf61b95cf9e	invoke:router	a13fd430-5796-11df-9093-dbf61b95cf9e
a3484e60-5796-11df-9093-dbf61b95cf9e	invoke:router	a13fd430-5796-11df-9093-dbf61b95cf9e
a34da590-5796-11df-9093-dbf61b95cf9e	invoke:router	a13fd430-5796-11df-9093-dbf61b95cf9e
a3528790-5796-11df-9093-dbf61b95cf9e	invoke:router	a13fd430-5796-11df-9093-dbf61b95cf9e
a357dec0-5796-11df-9093-dbf61b95cf9e	invoke:router	a13fd430-5796-11df-9093-dbf61b95cf9e
a35cc0c0-5796-11df-9093-dbf61b95cf9e	invoke:router	a13fd430-5796-11df-9093-dbf61b95cf9e
a361a2c0-5796-11df-9093-dbf61b95cf9e	invoke:router	a13fd430-5796-11df-9093-dbf61b95cf9e
a3672100-5796-11df-9093-dbf61b95cf9e	invoke:router	a13fd430-5796-11df-9093-dbf61b95cf9e
a36b66c0-5796-11df-9093-dbf61b95cf9e	invoke:router	a13fd430-5796-11df-9093-dbf61b95cf9e
a36fac80-5796-11df-9093-dbf61b95cf9e	invoke:router	a13fd430-5796-11df-9093-dbf61b95cf9e
a3741950-5796-11df-9093-dbf61b95cf9e	invoke:router	a13fd430-5796-11df-9093-dbf61b95cf9e
a379e5b0-5796-11df-9093-dbf61b95cf9e	invoke:router	a13fd430-5796-11df-9093-dbf61b95cf9e
a37e5280-5796-11df-9093-dbf61b95cf9e	invoke:router	a13fd430-5796-11df-9093-dbf61b95cf9e
a3827130-5796-11df-9093-dbf61b95cf9e	invoke:router	a13fd430-5796-11df-9093-dbf61b95cf9e
a38864a0-5796-11df-9093-dbf61b95cf9e	invoke:router	a13fd430-5796-11df-9093-dbf61b95cf9e
a38dbbd0-5796-11df-9093-dbf61b95cf9e	destroy:router	a13fd430-5796-11df-9093-dbf61b95cf9e
a3916550-5796-11df-9093-dbf61b95cf9e	getMultipleResourceProperties:router	a13fd430-5796-11df-9093-dbf61b95cf9e
a3964750-5796-11df-9093-dbf61b95cf9e	modify:router	a13fd430-5796-11df-9093-dbf61b95cf9e
a39b0240-5796-11df-9093-dbf61b95cf9e	getRouters:router	a13fd430-5796-11df-9093-dbf61b95cf9e
a39ed2d0-5796-11df-9093-dbf61b95cf9e	setPolicies:policies	a13fd430-5796-11df-9093-dbf61b95cf9e
a59bdb50-5796-11df-9093-dbf61b95cf9e	createNewRouterInstance:router	a3b1e5a0-5796-11df-9093-dbf61b95cf9e
a5a1cec0-5796-11df-9093-dbf61b95cf9e	setPollingPeriod:router	a3b1e5a0-5796-11df-9093-dbf61b95cf9e
a5a7e940-5796-11df-9093-dbf61b95cf9e	invoke:router	a3b1e5a0-5796-11df-9093-dbf61b95cf9e
a5addcb0-5796-11df-9093-dbf61b95cf9e	invoke:router	a3b1e5a0-5796-11df-9093-dbf61b95cf9e
a5b3a910-5796-11df-9093-dbf61b95cf9e	invoke:router	a3b1e5a0-5796-11df-9093-dbf61b95cf9e
a5b92750-5796-11df-9093-dbf61b95cf9e	invoke:router	a3b1e5a0-5796-11df-9093-dbf61b95cf9e
a5be7e80-5796-11df-9093-dbf61b95cf9e	invoke:router	a3b1e5a0-5796-11df-9093-dbf61b95cf9e
a5c905d0-5796-11df-9093-dbf61b95cf9e	invoke:router	a3b1e5a0-5796-11df-9093-dbf61b95cf9e
a5cd99b0-5796-11df-9093-dbf61b95cf9e	invoke:router	a3b1e5a0-5796-11df-9093-dbf61b95cf9e
a5d2c9d0-5796-11df-9093-dbf61b95cf9e	invoke:router	a3b1e5a0-5796-11df-9093-dbf61b95cf9e
a5d6c170-5796-11df-9093-dbf61b95cf9e	invoke:router	a3b1e5a0-5796-11df-9093-dbf61b95cf9e
a5dc66c0-5796-11df-9093-dbf61b95cf9e	invoke:router	a3b1e5a0-5796-11df-9093-dbf61b95cf9e
a5e0ac80-5796-11df-9093-dbf61b95cf9e	invoke:router	a3b1e5a0-5796-11df-9093-dbf61b95cf9e
a5e58e80-5796-11df-9093-dbf61b95cf9e	invoke:router	a3b1e5a0-5796-11df-9093-dbf61b95cf9e
a5eb33d0-5796-11df-9093-dbf61b95cf9e	invoke:router	a3b1e5a0-5796-11df-9093-dbf61b95cf9e
a5f10030-5796-11df-9093-dbf61b95cf9e	invoke:router	a3b1e5a0-5796-11df-9093-dbf61b95cf9e
a5f59410-5796-11df-9093-dbf61b95cf9e	invoke:router	a3b1e5a0-5796-11df-9093-dbf61b95cf9e
a5f9d9d0-5796-11df-9093-dbf61b95cf9e	invoke:router	a3b1e5a0-5796-11df-9093-dbf61b95cf9e
a600b7a0-5796-11df-9093-dbf61b95cf9e	invoke:router	a3b1e5a0-5796-11df-9093-dbf61b95cf9e
a60c0240-5796-11df-9093-dbf61b95cf9e	invoke:router	a3b1e5a0-5796-11df-9093-dbf61b95cf9e
a60fd2d0-5796-11df-9093-dbf61b95cf9e	destroy:router	a3b1e5a0-5796-11df-9093-dbf61b95cf9e
a613ca70-5796-11df-9093-dbf61b95cf9e	getMultipleResourceProperties:router	a3b1e5a0-5796-11df-9093-dbf61b95cf9e
a618fa90-5796-11df-9093-dbf61b95cf9e	modify:router	a3b1e5a0-5796-11df-9093-dbf61b95cf9e
a61cf230-5796-11df-9093-dbf61b95cf9e	getRouters:router	a3b1e5a0-5796-11df-9093-dbf61b95cf9e
a620c2c0-5796-11df-9093-dbf61b95cf9e	setPolicies:policies	a3b1e5a0-5796-11df-9093-dbf61b95cf9e
a8365c50-5796-11df-9093-dbf61b95cf9e	createNewRouterInstance:router	a63471d0-5796-11df-9093-dbf61b95cf9e
a83bda90-5796-11df-9093-dbf61b95cf9e	setPollingPeriod:router	a63471d0-5796-11df-9093-dbf61b95cf9e
a8417fe0-5796-11df-9093-dbf61b95cf9e	invoke:router	a63471d0-5796-11df-9093-dbf61b95cf9e
a8474c40-5796-11df-9093-dbf61b95cf9e	invoke:router	a63471d0-5796-11df-9093-dbf61b95cf9e
a84d66c0-5796-11df-9093-dbf61b95cf9e	invoke:router	a63471d0-5796-11df-9093-dbf61b95cf9e
a8533320-5796-11df-9093-dbf61b95cf9e	invoke:router	a63471d0-5796-11df-9093-dbf61b95cf9e
a8583c30-5796-11df-9093-dbf61b95cf9e	invoke:router	a63471d0-5796-11df-9093-dbf61b95cf9e
a85dba70-5796-11df-9093-dbf61b95cf9e	invoke:router	a63471d0-5796-11df-9093-dbf61b95cf9e
a8627560-5796-11df-9093-dbf61b95cf9e	invoke:router	a63471d0-5796-11df-9093-dbf61b95cf9e
a866bb20-5796-11df-9093-dbf61b95cf9e	invoke:router	a63471d0-5796-11df-9093-dbf61b95cf9e
a86b7610-5796-11df-9093-dbf61b95cf9e	invoke:router	a63471d0-5796-11df-9093-dbf61b95cf9e
a8711b60-5796-11df-9093-dbf61b95cf9e	invoke:router	a63471d0-5796-11df-9093-dbf61b95cf9e
a8751300-5796-11df-9093-dbf61b95cf9e	invoke:router	a63471d0-5796-11df-9093-dbf61b95cf9e
a87958c0-5796-11df-9093-dbf61b95cf9e	invoke:router	a63471d0-5796-11df-9093-dbf61b95cf9e
a87d7770-5796-11df-9093-dbf61b95cf9e	invoke:router	a63471d0-5796-11df-9093-dbf61b95cf9e
a882f5b0-5796-11df-9093-dbf61b95cf9e	invoke:router	a63471d0-5796-11df-9093-dbf61b95cf9e
a8878990-5796-11df-9093-dbf61b95cf9e	invoke:router	a63471d0-5796-11df-9093-dbf61b95cf9e
a88c1d70-5796-11df-9093-dbf61b95cf9e	invoke:router	a63471d0-5796-11df-9093-dbf61b95cf9e
a890d860-5796-11df-9093-dbf61b95cf9e	invoke:router	a63471d0-5796-11df-9093-dbf61b95cf9e
a8956c40-5796-11df-9093-dbf61b95cf9e	invoke:router	a63471d0-5796-11df-9093-dbf61b95cf9e
a898eeb0-5796-11df-9093-dbf61b95cf9e	destroy:router	a63471d0-5796-11df-9093-dbf61b95cf9e
a89d8290-5796-11df-9093-dbf61b95cf9e	getMultipleResourceProperties:router	a63471d0-5796-11df-9093-dbf61b95cf9e
a8a26490-5796-11df-9093-dbf61b95cf9e	modify:router	a63471d0-5796-11df-9093-dbf61b95cf9e
a8a5e700-5796-11df-9093-dbf61b95cf9e	getRouters:router	a63471d0-5796-11df-9093-dbf61b95cf9e
a8ac76b0-5796-11df-9093-dbf61b95cf9e	setPolicies:policies	a63471d0-5796-11df-9093-dbf61b95cf9e
aa981a10-5796-11df-9093-dbf61b95cf9e	createNewRouterInstance:router	a8bf3b60-5796-11df-9093-dbf61b95cf9e
aa9bc390-5796-11df-9093-dbf61b95cf9e	setPollingPeriod:router	a8bf3b60-5796-11df-9093-dbf61b95cf9e
aad3c380-5796-11df-9093-dbf61b95cf9e	invoke:router	a8bf3b60-5796-11df-9093-dbf61b95cf9e
aad85760-5796-11df-9093-dbf61b95cf9e	invoke:router	a8bf3b60-5796-11df-9093-dbf61b95cf9e
aae06db0-5796-11df-9093-dbf61b95cf9e	invoke:router	a8bf3b60-5796-11df-9093-dbf61b95cf9e
aae46550-5796-11df-9093-dbf61b95cf9e	invoke:router	a8bf3b60-5796-11df-9093-dbf61b95cf9e
aae96e60-5796-11df-9093-dbf61b95cf9e	invoke:router	a8bf3b60-5796-11df-9093-dbf61b95cf9e
aaeeeca0-5796-11df-9093-dbf61b95cf9e	invoke:router	a8bf3b60-5796-11df-9093-dbf61b95cf9e
aaf33260-5796-11df-9093-dbf61b95cf9e	invoke:router	a8bf3b60-5796-11df-9093-dbf61b95cf9e
aaf75110-5796-11df-9093-dbf61b95cf9e	invoke:router	a8bf3b60-5796-11df-9093-dbf61b95cf9e
aafc0c00-5796-11df-9093-dbf61b95cf9e	invoke:router	a8bf3b60-5796-11df-9093-dbf61b95cf9e
ab002ab0-5796-11df-9093-dbf61b95cf9e	invoke:router	a8bf3b60-5796-11df-9093-dbf61b95cf9e
ab044960-5796-11df-9093-dbf61b95cf9e	invoke:router	a8bf3b60-5796-11df-9093-dbf61b95cf9e
ab084100-5796-11df-9093-dbf61b95cf9e	invoke:router	a8bf3b60-5796-11df-9093-dbf61b95cf9e
ab0cfbf0-5796-11df-9093-dbf61b95cf9e	invoke:router	a8bf3b60-5796-11df-9093-dbf61b95cf9e
ab1168c0-5796-11df-9093-dbf61b95cf9e	invoke:router	a8bf3b60-5796-11df-9093-dbf61b95cf9e
ab153950-5796-11df-9093-dbf61b95cf9e	invoke:router	a8bf3b60-5796-11df-9093-dbf61b95cf9e
ab1a4260-5796-11df-9093-dbf61b95cf9e	invoke:router	a8bf3b60-5796-11df-9093-dbf61b95cf9e
ab1f2460-5796-11df-9093-dbf61b95cf9e	invoke:router	a8bf3b60-5796-11df-9093-dbf61b95cf9e
ab231c00-5796-11df-9093-dbf61b95cf9e	invoke:router	a8bf3b60-5796-11df-9093-dbf61b95cf9e
ab269e70-5796-11df-9093-dbf61b95cf9e	destroy:router	a8bf3b60-5796-11df-9093-dbf61b95cf9e
ab2a47f0-5796-11df-9093-dbf61b95cf9e	getMultipleResourceProperties:router	a8bf3b60-5796-11df-9093-dbf61b95cf9e
ab2eb4c0-5796-11df-9093-dbf61b95cf9e	modify:router	a8bf3b60-5796-11df-9093-dbf61b95cf9e
ab328550-5796-11df-9093-dbf61b95cf9e	getRouters:router	a8bf3b60-5796-11df-9093-dbf61b95cf9e
ab36a400-5796-11df-9093-dbf61b95cf9e	setPolicies:policies	a8bf3b60-5796-11df-9093-dbf61b95cf9e
ad5bf500-5796-11df-9093-dbf61b95cf9e	createNewRouterInstance:router	ab568810-5796-11df-9093-dbf61b95cf9e
ad636f10-5796-11df-9093-dbf61b95cf9e	setPollingPeriod:router	ab568810-5796-11df-9093-dbf61b95cf9e
ad685110-5796-11df-9093-dbf61b95cf9e	invoke:router	ab568810-5796-11df-9093-dbf61b95cf9e
ad6d5a20-5796-11df-9093-dbf61b95cf9e	invoke:router	ab568810-5796-11df-9093-dbf61b95cf9e
ad721510-5796-11df-9093-dbf61b95cf9e	invoke:router	ab568810-5796-11df-9093-dbf61b95cf9e
ad78a4c0-5796-11df-9093-dbf61b95cf9e	invoke:router	ab568810-5796-11df-9093-dbf61b95cf9e
ad7cc370-5796-11df-9093-dbf61b95cf9e	invoke:router	ab568810-5796-11df-9093-dbf61b95cf9e
ad81cc80-5796-11df-9093-dbf61b95cf9e	invoke:router	ab568810-5796-11df-9093-dbf61b95cf9e
ad8771d0-5796-11df-9093-dbf61b95cf9e	invoke:router	ab568810-5796-11df-9093-dbf61b95cf9e
ad8cc900-5796-11df-9093-dbf61b95cf9e	invoke:router	ab568810-5796-11df-9093-dbf61b95cf9e
ad91f920-5796-11df-9093-dbf61b95cf9e	invoke:router	ab568810-5796-11df-9093-dbf61b95cf9e
ad968d00-5796-11df-9093-dbf61b95cf9e	invoke:router	ab568810-5796-11df-9093-dbf61b95cf9e
ad9b20e0-5796-11df-9093-dbf61b95cf9e	invoke:router	ab568810-5796-11df-9093-dbf61b95cf9e
ada13b60-5796-11df-9093-dbf61b95cf9e	invoke:router	ab568810-5796-11df-9093-dbf61b95cf9e
ada61d60-5796-11df-9093-dbf61b95cf9e	invoke:router	ab568810-5796-11df-9093-dbf61b95cf9e
adac5ef0-5796-11df-9093-dbf61b95cf9e	invoke:router	ab568810-5796-11df-9093-dbf61b95cf9e
adb0cbc0-5796-11df-9093-dbf61b95cf9e	invoke:router	ab568810-5796-11df-9093-dbf61b95cf9e
adb69820-5796-11df-9093-dbf61b95cf9e	invoke:router	ab568810-5796-11df-9093-dbf61b95cf9e
adbb04f0-5796-11df-9093-dbf61b95cf9e	invoke:router	ab568810-5796-11df-9093-dbf61b95cf9e
adc00e00-5796-11df-9093-dbf61b95cf9e	invoke:router	ab568810-5796-11df-9093-dbf61b95cf9e
adc3de90-5796-11df-9093-dbf61b95cf9e	destroy:router	ab568810-5796-11df-9093-dbf61b95cf9e
adc87270-5796-11df-9093-dbf61b95cf9e	getMultipleResourceProperties:router	ab568810-5796-11df-9093-dbf61b95cf9e
adcdc9a0-5796-11df-9093-dbf61b95cf9e	modify:router	ab568810-5796-11df-9093-dbf61b95cf9e
add14c10-5796-11df-9093-dbf61b95cf9e	getRouters:router	ab568810-5796-11df-9093-dbf61b95cf9e
add51ca0-5796-11df-9093-dbf61b95cf9e	setPolicies:policies	ab568810-5796-11df-9093-dbf61b95cf9e
afff2890-5796-11df-9093-dbf61b95cf9e	createNewRouterInstance:router	adf3ef40-5796-11df-9093-dbf61b95cf9e
b0056a20-5796-11df-9093-dbf61b95cf9e	setPollingPeriod:router	adf3ef40-5796-11df-9093-dbf61b95cf9e
b00b3680-5796-11df-9093-dbf61b95cf9e	invoke:router	adf3ef40-5796-11df-9093-dbf61b95cf9e
b0103f90-5796-11df-9093-dbf61b95cf9e	invoke:router	adf3ef40-5796-11df-9093-dbf61b95cf9e
b018cb10-5796-11df-9093-dbf61b95cf9e	invoke:router	adf3ef40-5796-11df-9093-dbf61b95cf9e
b01e2240-5796-11df-9093-dbf61b95cf9e	invoke:router	adf3ef40-5796-11df-9093-dbf61b95cf9e
b0230440-5796-11df-9093-dbf61b95cf9e	invoke:router	adf3ef40-5796-11df-9093-dbf61b95cf9e
b028f7b0-5796-11df-9093-dbf61b95cf9e	invoke:router	adf3ef40-5796-11df-9093-dbf61b95cf9e
b02e9d00-5796-11df-9093-dbf61b95cf9e	invoke:router	adf3ef40-5796-11df-9093-dbf61b95cf9e
b033a610-5796-11df-9093-dbf61b95cf9e	invoke:router	adf3ef40-5796-11df-9093-dbf61b95cf9e
b037ebd0-5796-11df-9093-dbf61b95cf9e	invoke:router	adf3ef40-5796-11df-9093-dbf61b95cf9e
b03c0a80-5796-11df-9093-dbf61b95cf9e	invoke:router	adf3ef40-5796-11df-9093-dbf61b95cf9e
b04188c0-5796-11df-9093-dbf61b95cf9e	invoke:router	adf3ef40-5796-11df-9093-dbf61b95cf9e
b0461ca0-5796-11df-9093-dbf61b95cf9e	invoke:router	adf3ef40-5796-11df-9093-dbf61b95cf9e
b04a8970-5796-11df-9093-dbf61b95cf9e	invoke:router	adf3ef40-5796-11df-9093-dbf61b95cf9e
b04f4460-5796-11df-9093-dbf61b95cf9e	invoke:router	adf3ef40-5796-11df-9093-dbf61b95cf9e
b055ad00-5796-11df-9093-dbf61b95cf9e	invoke:router	adf3ef40-5796-11df-9093-dbf61b95cf9e
b05a19d0-5796-11df-9093-dbf61b95cf9e	invoke:router	adf3ef40-5796-11df-9093-dbf61b95cf9e
b05e1170-5796-11df-9093-dbf61b95cf9e	invoke:router	adf3ef40-5796-11df-9093-dbf61b95cf9e
b0647a10-5796-11df-9093-dbf61b95cf9e	invoke:router	adf3ef40-5796-11df-9093-dbf61b95cf9e
b0698320-5796-11df-9093-dbf61b95cf9e	destroy:router	adf3ef40-5796-11df-9093-dbf61b95cf9e
b06dc8e0-5796-11df-9093-dbf61b95cf9e	getMultipleResourceProperties:router	adf3ef40-5796-11df-9093-dbf61b95cf9e
b0720ea0-5796-11df-9093-dbf61b95cf9e	modify:router	adf3ef40-5796-11df-9093-dbf61b95cf9e
b0762d50-5796-11df-9093-dbf61b95cf9e	getRouters:router	adf3ef40-5796-11df-9093-dbf61b95cf9e
b07b0f50-5796-11df-9093-dbf61b95cf9e	setPolicies:policies	adf3ef40-5796-11df-9093-dbf61b95cf9e
b283d7a0-5796-11df-9093-dbf61b95cf9e	createNewRouterInstance:router	b096ada0-5796-11df-9093-dbf61b95cf9e
b288b9a0-5796-11df-9093-dbf61b95cf9e	setPollingPeriod:router	b096ada0-5796-11df-9093-dbf61b95cf9e
b28cff60-5796-11df-9093-dbf61b95cf9e	invoke:router	b096ada0-5796-11df-9093-dbf61b95cf9e
b2920870-5796-11df-9093-dbf61b95cf9e	invoke:router	b096ada0-5796-11df-9093-dbf61b95cf9e
b2969c50-5796-11df-9093-dbf61b95cf9e	invoke:router	b096ada0-5796-11df-9093-dbf61b95cf9e
b29ae210-5796-11df-9093-dbf61b95cf9e	invoke:router	b096ada0-5796-11df-9093-dbf61b95cf9e
b29f4ee0-5796-11df-9093-dbf61b95cf9e	invoke:router	b096ada0-5796-11df-9093-dbf61b95cf9e
b2a56960-5796-11df-9093-dbf61b95cf9e	invoke:router	b096ada0-5796-11df-9093-dbf61b95cf9e
b2aac090-5796-11df-9093-dbf61b95cf9e	invoke:router	b096ada0-5796-11df-9093-dbf61b95cf9e
b2af0650-5796-11df-9093-dbf61b95cf9e	invoke:router	b096ada0-5796-11df-9093-dbf61b95cf9e
b2b37320-5796-11df-9093-dbf61b95cf9e	invoke:router	b096ada0-5796-11df-9093-dbf61b95cf9e
b2b82e10-5796-11df-9093-dbf61b95cf9e	invoke:router	b096ada0-5796-11df-9093-dbf61b95cf9e
b2bc9ae0-5796-11df-9093-dbf61b95cf9e	invoke:router	b096ada0-5796-11df-9093-dbf61b95cf9e
b2c0b990-5796-11df-9093-dbf61b95cf9e	invoke:router	b096ada0-5796-11df-9093-dbf61b95cf9e
b2c5c2a0-5796-11df-9093-dbf61b95cf9e	invoke:router	b096ada0-5796-11df-9093-dbf61b95cf9e
b2ca2f70-5796-11df-9093-dbf61b95cf9e	invoke:router	b096ada0-5796-11df-9093-dbf61b95cf9e
b2cf3880-5796-11df-9093-dbf61b95cf9e	invoke:router	b096ada0-5796-11df-9093-dbf61b95cf9e
b2d3f370-5796-11df-9093-dbf61b95cf9e	invoke:router	b096ada0-5796-11df-9093-dbf61b95cf9e
b2d83930-5796-11df-9093-dbf61b95cf9e	invoke:router	b096ada0-5796-11df-9093-dbf61b95cf9e
b2dd4240-5796-11df-9093-dbf61b95cf9e	invoke:router	b096ada0-5796-11df-9093-dbf61b95cf9e
b2e1af10-5796-11df-9093-dbf61b95cf9e	destroy:router	b096ada0-5796-11df-9093-dbf61b95cf9e
b2e75460-5796-11df-9093-dbf61b95cf9e	getMultipleResourceProperties:router	b096ada0-5796-11df-9093-dbf61b95cf9e
b2ead6d0-5796-11df-9093-dbf61b95cf9e	modify:router	b096ada0-5796-11df-9093-dbf61b95cf9e
b2ee8050-5796-11df-9093-dbf61b95cf9e	getRouters:router	b096ada0-5796-11df-9093-dbf61b95cf9e
b2f2ed20-5796-11df-9093-dbf61b95cf9e	setPolicies:policies	b096ada0-5796-11df-9093-dbf61b95cf9e
b4e9db20-5796-11df-9093-dbf61b95cf9e	createNewRouterInstance:router	b3073870-5796-11df-9093-dbf61b95cf9e
b4efce90-5796-11df-9093-dbf61b95cf9e	setPollingPeriod:router	b3073870-5796-11df-9093-dbf61b95cf9e
b4f54cd0-5796-11df-9093-dbf61b95cf9e	invoke:router	b3073870-5796-11df-9093-dbf61b95cf9e
b4fc9fd0-5796-11df-9093-dbf61b95cf9e	invoke:router	b3073870-5796-11df-9093-dbf61b95cf9e
b503f2d0-5796-11df-9093-dbf61b95cf9e	invoke:router	b3073870-5796-11df-9093-dbf61b95cf9e
b508fbe0-5796-11df-9093-dbf61b95cf9e	invoke:router	b3073870-5796-11df-9093-dbf61b95cf9e
b50db6d0-5796-11df-9093-dbf61b95cf9e	invoke:router	b3073870-5796-11df-9093-dbf61b95cf9e
b5161b40-5796-11df-9093-dbf61b95cf9e	invoke:router	b3073870-5796-11df-9093-dbf61b95cf9e
b51aaf20-5796-11df-9093-dbf61b95cf9e	invoke:router	b3073870-5796-11df-9093-dbf61b95cf9e
b51ef4e0-5796-11df-9093-dbf61b95cf9e	invoke:router	b3073870-5796-11df-9093-dbf61b95cf9e
b5233aa0-5796-11df-9093-dbf61b95cf9e	invoke:router	b3073870-5796-11df-9093-dbf61b95cf9e
b5295520-5796-11df-9093-dbf61b95cf9e	invoke:router	b3073870-5796-11df-9093-dbf61b95cf9e
b52e3720-5796-11df-9093-dbf61b95cf9e	invoke:router	b3073870-5796-11df-9093-dbf61b95cf9e
b5336740-5796-11df-9093-dbf61b95cf9e	invoke:router	b3073870-5796-11df-9093-dbf61b95cf9e
b537ad00-5796-11df-9093-dbf61b95cf9e	invoke:router	b3073870-5796-11df-9093-dbf61b95cf9e
b53ed8f0-5796-11df-9093-dbf61b95cf9e	invoke:router	b3073870-5796-11df-9093-dbf61b95cf9e
b5436cd0-5796-11df-9093-dbf61b95cf9e	invoke:router	b3073870-5796-11df-9093-dbf61b95cf9e
b5478b80-5796-11df-9093-dbf61b95cf9e	invoke:router	b3073870-5796-11df-9093-dbf61b95cf9e
b54b8320-5796-11df-9093-dbf61b95cf9e	invoke:router	b3073870-5796-11df-9093-dbf61b95cf9e
b550da50-5796-11df-9093-dbf61b95cf9e	invoke:router	b3073870-5796-11df-9093-dbf61b95cf9e
b55483d0-5796-11df-9093-dbf61b95cf9e	destroy:router	b3073870-5796-11df-9093-dbf61b95cf9e
b55965d0-5796-11df-9093-dbf61b95cf9e	getMultipleResourceProperties:router	b3073870-5796-11df-9093-dbf61b95cf9e
b55d0f50-5796-11df-9093-dbf61b95cf9e	modify:router	b3073870-5796-11df-9093-dbf61b95cf9e
b5626680-5796-11df-9093-dbf61b95cf9e	getRouters:router	b3073870-5796-11df-9093-dbf61b95cf9e
b5672170-5796-11df-9093-dbf61b95cf9e	setPolicies:policies	b3073870-5796-11df-9093-dbf61b95cf9e
b76429f0-5796-11df-9093-dbf61b95cf9e	createNewRouterInstance:router	b57949e0-5796-11df-9093-dbf61b95cf9e
b76a9290-5796-11df-9093-dbf61b95cf9e	setPollingPeriod:router	b57949e0-5796-11df-9093-dbf61b95cf9e
b76eb140-5796-11df-9093-dbf61b95cf9e	invoke:router	b57949e0-5796-11df-9093-dbf61b95cf9e
b7731e10-5796-11df-9093-dbf61b95cf9e	invoke:router	b57949e0-5796-11df-9093-dbf61b95cf9e
b77715b0-5796-11df-9093-dbf61b95cf9e	invoke:router	b57949e0-5796-11df-9093-dbf61b95cf9e
b77dcc70-5796-11df-9093-dbf61b95cf9e	invoke:router	b57949e0-5796-11df-9093-dbf61b95cf9e
b782d580-5796-11df-9093-dbf61b95cf9e	invoke:router	b57949e0-5796-11df-9093-dbf61b95cf9e
b7871b40-5796-11df-9093-dbf61b95cf9e	invoke:router	b57949e0-5796-11df-9093-dbf61b95cf9e
b78b8810-5796-11df-9093-dbf61b95cf9e	invoke:router	b57949e0-5796-11df-9093-dbf61b95cf9e
b7904300-5796-11df-9093-dbf61b95cf9e	invoke:router	b57949e0-5796-11df-9093-dbf61b95cf9e
b7943aa0-5796-11df-9093-dbf61b95cf9e	invoke:router	b57949e0-5796-11df-9093-dbf61b95cf9e
b7988060-5796-11df-9093-dbf61b95cf9e	invoke:router	b57949e0-5796-11df-9093-dbf61b95cf9e
b79d8970-5796-11df-9093-dbf61b95cf9e	invoke:router	b57949e0-5796-11df-9093-dbf61b95cf9e
b7a26b70-5796-11df-9093-dbf61b95cf9e	invoke:router	b57949e0-5796-11df-9093-dbf61b95cf9e
b7a79b90-5796-11df-9093-dbf61b95cf9e	invoke:router	b57949e0-5796-11df-9093-dbf61b95cf9e
b7b2bf20-5796-11df-9093-dbf61b95cf9e	invoke:router	b57949e0-5796-11df-9093-dbf61b95cf9e
b7b704e0-5796-11df-9093-dbf61b95cf9e	invoke:router	b57949e0-5796-11df-9093-dbf61b95cf9e
b7bc5c10-5796-11df-9093-dbf61b95cf9e	invoke:router	b57949e0-5796-11df-9093-dbf61b95cf9e
b7c02ca0-5796-11df-9093-dbf61b95cf9e	invoke:router	b57949e0-5796-11df-9093-dbf61b95cf9e
b7c3fd30-5796-11df-9093-dbf61b95cf9e	invoke:router	b57949e0-5796-11df-9093-dbf61b95cf9e
b7c89110-5796-11df-9093-dbf61b95cf9e	destroy:router	b57949e0-5796-11df-9093-dbf61b95cf9e
b7cd24f0-5796-11df-9093-dbf61b95cf9e	getMultipleResourceProperties:router	b57949e0-5796-11df-9093-dbf61b95cf9e
b7d0a760-5796-11df-9093-dbf61b95cf9e	modify:router	b57949e0-5796-11df-9093-dbf61b95cf9e
b7d402c0-5796-11df-9093-dbf61b95cf9e	getRouters:router	b57949e0-5796-11df-9093-dbf61b95cf9e
b7d86f90-5796-11df-9093-dbf61b95cf9e	setPolicies:policies	b57949e0-5796-11df-9093-dbf61b95cf9e
ba162a90-5796-11df-9093-dbf61b95cf9e	createNewRouterInstance:router	b7eb5b50-5796-11df-9093-dbf61b95cf9e
ba1b5ab0-5796-11df-9093-dbf61b95cf9e	setPollingPeriod:router	b7eb5b50-5796-11df-9093-dbf61b95cf9e
ba20b1e0-5796-11df-9093-dbf61b95cf9e	invoke:router	b7eb5b50-5796-11df-9093-dbf61b95cf9e
ba265730-5796-11df-9093-dbf61b95cf9e	invoke:router	b7eb5b50-5796-11df-9093-dbf61b95cf9e
ba2d8320-5796-11df-9093-dbf61b95cf9e	invoke:router	b7eb5b50-5796-11df-9093-dbf61b95cf9e
ba326520-5796-11df-9093-dbf61b95cf9e	invoke:router	b7eb5b50-5796-11df-9093-dbf61b95cf9e
ba3683d0-5796-11df-9093-dbf61b95cf9e	invoke:router	b7eb5b50-5796-11df-9093-dbf61b95cf9e
ba3b65d0-5796-11df-9093-dbf61b95cf9e	invoke:router	b7eb5b50-5796-11df-9093-dbf61b95cf9e
ba413230-5796-11df-9093-dbf61b95cf9e	invoke:router	b7eb5b50-5796-11df-9093-dbf61b95cf9e
ba4550e0-5796-11df-9093-dbf61b95cf9e	invoke:router	b7eb5b50-5796-11df-9093-dbf61b95cf9e
ba49bdb0-5796-11df-9093-dbf61b95cf9e	invoke:router	b7eb5b50-5796-11df-9093-dbf61b95cf9e
ba4db550-5796-11df-9093-dbf61b95cf9e	invoke:router	b7eb5b50-5796-11df-9093-dbf61b95cf9e
ba546c10-5796-11df-9093-dbf61b95cf9e	invoke:router	b7eb5b50-5796-11df-9093-dbf61b95cf9e
ba5ad4b0-5796-11df-9093-dbf61b95cf9e	invoke:router	b7eb5b50-5796-11df-9093-dbf61b95cf9e
ba5f1a70-5796-11df-9093-dbf61b95cf9e	invoke:router	b7eb5b50-5796-11df-9093-dbf61b95cf9e
ba642380-5796-11df-9093-dbf61b95cf9e	invoke:router	b7eb5b50-5796-11df-9093-dbf61b95cf9e
ba681b20-5796-11df-9093-dbf61b95cf9e	invoke:router	b7eb5b50-5796-11df-9093-dbf61b95cf9e
ba6e5cb0-5796-11df-9093-dbf61b95cf9e	invoke:router	b7eb5b50-5796-11df-9093-dbf61b95cf9e
ba72c980-5796-11df-9093-dbf61b95cf9e	invoke:router	b7eb5b50-5796-11df-9093-dbf61b95cf9e
ba76c120-5796-11df-9093-dbf61b95cf9e	invoke:router	b7eb5b50-5796-11df-9093-dbf61b95cf9e
ba7adfd0-5796-11df-9093-dbf61b95cf9e	destroy:router	b7eb5b50-5796-11df-9093-dbf61b95cf9e
ba7f4ca0-5796-11df-9093-dbf61b95cf9e	getMultipleResourceProperties:router	b7eb5b50-5796-11df-9093-dbf61b95cf9e
ba8455b0-5796-11df-9093-dbf61b95cf9e	modify:router	b7eb5b50-5796-11df-9093-dbf61b95cf9e
ba87d820-5796-11df-9093-dbf61b95cf9e	getRouters:router	b7eb5b50-5796-11df-9093-dbf61b95cf9e
ba8b81a0-5796-11df-9093-dbf61b95cf9e	setPolicies:policies	b7eb5b50-5796-11df-9093-dbf61b95cf9e
\.


--
-- Data for Name: router_instance; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY router_instance (router_resource_id, router_name, model, parent, polling_period, "location", accessconfiguration) FROM stdin;
9bde6b00-5796-11df-9093-dbf61b95cf9e	R-AS2-1	ROUTER_JUNIPERM20	9856a3d0-5796-11df-9093-dbf61b95cf9e	0	1	1
9eaca630-5796-11df-9093-dbf61b95cf9e	R-AS2-2	ROUTER_JUNIPERM20	9856a3d0-5796-11df-9093-dbf61b95cf9e	0	2	2
a13fd430-5796-11df-9093-dbf61b95cf9e	R-AS2-3	ROUTER_JUNIPERM20	9856a3d0-5796-11df-9093-dbf61b95cf9e	0	3	3
a3b1e5a0-5796-11df-9093-dbf61b95cf9e	R1	ROUTER_JUNIPERM20	9856a3d0-5796-11df-9093-dbf61b95cf9e	0	4	4
a63471d0-5796-11df-9093-dbf61b95cf9e	R2	ROUTER_JUNIPERM20	9856a3d0-5796-11df-9093-dbf61b95cf9e	0	5	5
a8bf3b60-5796-11df-9093-dbf61b95cf9e	R3	ROUTER_JUNIPERM20	9856a3d0-5796-11df-9093-dbf61b95cf9e	0	6	6
ab568810-5796-11df-9093-dbf61b95cf9e	R4	ROUTER_JUNIPERM20	9856a3d0-5796-11df-9093-dbf61b95cf9e	0	7	7
adf3ef40-5796-11df-9093-dbf61b95cf9e	R5	ROUTER_JUNIPERM20	9856a3d0-5796-11df-9093-dbf61b95cf9e	0	8	8
b096ada0-5796-11df-9093-dbf61b95cf9e	R6	ROUTER_JUNIPERM20	9856a3d0-5796-11df-9093-dbf61b95cf9e	0	9	9
b3073870-5796-11df-9093-dbf61b95cf9e	R7	ROUTER_JUNIPERM20	9856a3d0-5796-11df-9093-dbf61b95cf9e	0	10	10
b57949e0-5796-11df-9093-dbf61b95cf9e	R8	ROUTER_JUNIPERM20	9856a3d0-5796-11df-9093-dbf61b95cf9e	0	11	11
b7eb5b50-5796-11df-9093-dbf61b95cf9e	RLIE	ROUTER_JUNIPERM20	9856a3d0-5796-11df-9093-dbf61b95cf9e	0	12	12
9856a3d0-5796-11df-9093-dbf61b95cf9e	heanet	ROUTER_JUNIPERM20	\N	0	13	13
\.


--
-- Data for Name: router_instance_access_configuration; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY router_instance_access_configuration (access_configuration_id, ip_access_address, port, transport_name, protocol_name) FROM stdin;
1	193.1.190.254	22	SSH	NETCONF
2	193.1.190.254	22	SSH	NETCONF
3	193.1.190.254	22	SSH	NETCONF
4	193.1.190.254	22	SSH	NETCONF
5	193.1.190.254	22	SSH	NETCONF
6	193.1.190.254	22	SSH	NETCONF
7	193.1.190.254	22	SSH	NETCONF
8	193.1.190.254	22	SSH	NETCONF
9	193.1.190.254	22	SSH	NETCONF
10	193.1.190.254	22	SSH	NETCONF
11	193.1.190.254	22	SSH	NETCONF
12	193.1.190.254	22	SSH	NETCONF
13	193.1.190.254	22	SSH	NETCONF
\.


--
-- Data for Name: router_instance_children; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY router_instance_children (router_instance_id, children_key, "position") FROM stdin;
9856a3d0-5796-11df-9093-dbf61b95cf9e	9bde6b00-5796-11df-9093-dbf61b95cf9e	0
9856a3d0-5796-11df-9093-dbf61b95cf9e	9eaca630-5796-11df-9093-dbf61b95cf9e	1
9856a3d0-5796-11df-9093-dbf61b95cf9e	a13fd430-5796-11df-9093-dbf61b95cf9e	2
9856a3d0-5796-11df-9093-dbf61b95cf9e	a3b1e5a0-5796-11df-9093-dbf61b95cf9e	3
9856a3d0-5796-11df-9093-dbf61b95cf9e	a63471d0-5796-11df-9093-dbf61b95cf9e	4
9856a3d0-5796-11df-9093-dbf61b95cf9e	a8bf3b60-5796-11df-9093-dbf61b95cf9e	5
9856a3d0-5796-11df-9093-dbf61b95cf9e	ab568810-5796-11df-9093-dbf61b95cf9e	6
9856a3d0-5796-11df-9093-dbf61b95cf9e	adf3ef40-5796-11df-9093-dbf61b95cf9e	7
9856a3d0-5796-11df-9093-dbf61b95cf9e	b096ada0-5796-11df-9093-dbf61b95cf9e	8
9856a3d0-5796-11df-9093-dbf61b95cf9e	b3073870-5796-11df-9093-dbf61b95cf9e	9
9856a3d0-5796-11df-9093-dbf61b95cf9e	b57949e0-5796-11df-9093-dbf61b95cf9e	10
9856a3d0-5796-11df-9093-dbf61b95cf9e	b7eb5b50-5796-11df-9093-dbf61b95cf9e	11
\.


--
-- Data for Name: router_instance_location_type; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY router_instance_location_type (location_id, city, country, address, telephone, email, latitude, longitude, time_zone) FROM stdin;
1						0	0	
2						0	0	
3						0	0	
4						0	0	
5						0	0	
6						0	0	
7						0	0	
8						0	0	
9						0	0	
10						0	0	
11						0	0	
12						0	0	
13						0	0	
\.


--
-- Data for Name: router_instance_user_account; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY router_instance_user_account (user_account_id, user_id, psw, privileged_user, privileged_psw, router_configured, smtp_server, smtp_server_port, email_user, email_password, router_instance_id) FROM stdin;
1	i2cat	mant6WWe			R-AS2-1	\N	\N	\N	\N	9bde6b00-5796-11df-9093-dbf61b95cf9e
2	i2cat	mant6WWe			R-AS2-2	\N	\N	\N	\N	9eaca630-5796-11df-9093-dbf61b95cf9e
3	i2cat	mant6WWe			R-AS2-3	\N	\N	\N	\N	a13fd430-5796-11df-9093-dbf61b95cf9e
4	i2cat	mant6WWe			R1	\N	\N	\N	\N	a3b1e5a0-5796-11df-9093-dbf61b95cf9e
5	i2cat	mant6WWe			R2	\N	\N	\N	\N	a63471d0-5796-11df-9093-dbf61b95cf9e
6	i2cat	mant6WWe			R3	\N	\N	\N	\N	a8bf3b60-5796-11df-9093-dbf61b95cf9e
7	i2cat	mant6WWe			R4	\N	\N	\N	\N	ab568810-5796-11df-9093-dbf61b95cf9e
8	i2cat	mant6WWe			R5	\N	\N	\N	\N	adf3ef40-5796-11df-9093-dbf61b95cf9e
9	i2cat	mant6WWe			R6	\N	\N	\N	\N	b096ada0-5796-11df-9093-dbf61b95cf9e
10	i2cat	mant6WWe			R7	\N	\N	\N	\N	b3073870-5796-11df-9093-dbf61b95cf9e
11	i2cat	mant6WWe			R8	\N	\N	\N	\N	b57949e0-5796-11df-9093-dbf61b95cf9e
12	i2cat	mant6WWe			RLIE	\N	\N	\N	\N	b7eb5b50-5796-11df-9093-dbf61b95cf9e
13	i2cat	mant6WWe			\N	\N	\N	\N	\N	9856a3d0-5796-11df-9093-dbf61b95cf9e
\.


--
-- Data for Name: user_instance; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY user_instance (resource_id, user_name, first_name, last_name, organization, address, telephone, email, "role") FROM stdin;
20c31150-5796-11df-9093-dbf61b95cf9e	i2cat	i2cat	i2cat	ITI				Administrator
7e04a9f0-5796-11df-9093-dbf61b95cf9e	i2cat-user	i2cat-user	i2cat-user	ITI				User
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

