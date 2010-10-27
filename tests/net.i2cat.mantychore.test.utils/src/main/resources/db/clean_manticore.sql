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
186b7ec0-3027-11df-85fd-d0968b3d1792	ipnet	Manhattan
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
186b7ec0-3027-11df-85fd-d0968b3d1792	b4f9bb40-3026-11df-85fd-d0968b3d1792	https://io.argia.i2cat.net:8443/wsrf/services/manticore/RouterResourceService	0
186b7ec0-3027-11df-85fd-d0968b3d1792	b85c2020-3026-11df-85fd-d0968b3d1792	https://io.argia.i2cat.net:8443/wsrf/services/manticore/RouterResourceService	1
186b7ec0-3027-11df-85fd-d0968b3d1792	bba6b740-3026-11df-85fd-d0968b3d1792	https://io.argia.i2cat.net:8443/wsrf/services/manticore/RouterResourceService	2
186b7ec0-3027-11df-85fd-d0968b3d1792	bef2fc10-3026-11df-85fd-d0968b3d1792	https://io.argia.i2cat.net:8443/wsrf/services/manticore/RouterResourceService	3
186b7ec0-3027-11df-85fd-d0968b3d1792	c5b22080-3026-11df-85fd-d0968b3d1792	https://io.argia.i2cat.net:8443/wsrf/services/manticore/RouterResourceService	4
186b7ec0-3027-11df-85fd-d0968b3d1792	c259cdc0-3026-11df-85fd-d0968b3d1792	https://io.argia.i2cat.net:8443/wsrf/services/manticore/RouterResourceService	5
186b7ec0-3027-11df-85fd-d0968b3d1792	c8ce54a0-3026-11df-85fd-d0968b3d1792	https://io.argia.i2cat.net:8443/wsrf/services/manticore/RouterResourceService	6
186b7ec0-3027-11df-85fd-d0968b3d1792	cbee3240-3026-11df-85fd-d0968b3d1792	https://io.argia.i2cat.net:8443/wsrf/services/manticore/RouterResourceService	7
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
9516d930-3025-11df-bd6b-a6bcc20293a6		0
9524bbe0-3025-11df-bd6b-a6bcc20293a6		0
9527c920-3025-11df-bd6b-a6bcc20293a6		0
6b3489e0-3026-11df-85fd-d0968b3d1792		0
6b3a0820-3026-11df-85fd-d0968b3d1792		0
6b4293a0-3026-11df-85fd-d0968b3d1792		0
6b4aa9f0-3026-11df-85fd-d0968b3d1792		0
6b5187c0-3026-11df-85fd-d0968b3d1792		0
6b583e80-3026-11df-85fd-d0968b3d1792		0
93174330-3026-11df-85fd-d0968b3d1792		0
931fceb0-3026-11df-85fd-d0968b3d1792		0
9326ac80-3026-11df-85fd-d0968b3d1792		0
932c03b0-3026-11df-85fd-d0968b3d1792		0
93324540-3026-11df-85fd-d0968b3d1792		0
933811a0-3026-11df-85fd-d0968b3d1792		0
b49a8440-3026-11df-85fd-d0968b3d1792		0
b49fdb70-3026-11df-85fd-d0968b3d1792		0
b4a75580-3026-11df-85fd-d0968b3d1792		0
b4ac3780-3026-11df-85fd-d0968b3d1792	CONFIGURE_INTERFACES	0
b4b11980-3026-11df-85fd-d0968b3d1792	DELETE_LOGICAL_INTERFACE	0
b4b697c0-3026-11df-85fd-d0968b3d1792	REFRESH	0
b4bc1600-3026-11df-85fd-d0968b3d1792	prepare-commit	0
b4c2ccc0-3026-11df-85fd-d0968b3d1792	confirm-commit	0
b4c8c030-3026-11df-85fd-d0968b3d1792	rollback-commit	0
b4cdc940-3026-11df-85fd-d0968b3d1792		0
b4d2d250-3026-11df-85fd-d0968b3d1792		0
b4d67bd0-3026-11df-85fd-d0968b3d1792		0
b4dae8a0-3026-11df-85fd-d0968b3d1792		0
b4deb930-3026-11df-85fd-d0968b3d1792		0
b7e47f20-3026-11df-85fd-d0968b3d1792		0
b7eac0b0-3026-11df-85fd-d0968b3d1792		0
b7f0b420-3026-11df-85fd-d0968b3d1792		0
b7f59620-3026-11df-85fd-d0968b3d1792	CONFIGURE_INTERFACES	0
b80a0880-3026-11df-85fd-d0968b3d1792	DELETE_LOGICAL_INTERFACE	0
b8115b80-3026-11df-85fd-d0968b3d1792	REFRESH	0
b8157a30-3026-11df-85fd-d0968b3d1792	prepare-commit	0
b81b6da0-3026-11df-85fd-d0968b3d1792	confirm-commit	0
b821af30-3026-11df-85fd-d0968b3d1792	rollback-commit	0
b82531a0-3026-11df-85fd-d0968b3d1792		0
b82ad6f0-3026-11df-85fd-d0968b3d1792		0
b82f1cb0-3026-11df-85fd-d0968b3d1792		0
b832ed40-3026-11df-85fd-d0968b3d1792		0
b83696c0-3026-11df-85fd-d0968b3d1792		0
bb5c19b0-3026-11df-85fd-d0968b3d1792		0
bb608680-3026-11df-85fd-d0968b3d1792		0
bb662bd0-3026-11df-85fd-d0968b3d1792		0
bb6a7190-3026-11df-85fd-d0968b3d1792	CONFIGURE_INTERFACES	0
bb6f7aa0-3026-11df-85fd-d0968b3d1792	DELETE_LOGICAL_INTERFACE	0
bb75bc30-3026-11df-85fd-d0968b3d1792	REFRESH	0
bb79b3d0-3026-11df-85fd-d0968b3d1792	prepare-commit	0
bb7ebce0-3026-11df-85fd-d0968b3d1792	confirm-commit	0
bb8573a0-3026-11df-85fd-d0968b3d1792	rollback-commit	0
bb8a0780-3026-11df-85fd-d0968b3d1792		0
bb8d89f0-3026-11df-85fd-d0968b3d1792		0
bb913370-3026-11df-85fd-d0968b3d1792		0
bb96b1b0-3026-11df-85fd-d0968b3d1792		0
bb9a8240-3026-11df-85fd-d0968b3d1792		0
bea689c0-3026-11df-85fd-d0968b3d1792		0
bead1970-3026-11df-85fd-d0968b3d1792		0
beb13820-3026-11df-85fd-d0968b3d1792		0
beb556d0-3026-11df-85fd-d0968b3d1792	CONFIGURE_INTERFACES	0
beba5fe0-3026-11df-85fd-d0968b3d1792	DELETE_LOGICAL_INTERFACE	0
bebf41e0-3026-11df-85fd-d0968b3d1792	REFRESH	0
bec33980-3026-11df-85fd-d0968b3d1792	prepare-commit	0
bec92cf0-3026-11df-85fd-d0968b3d1792	confirm-commit	0
becd4ba0-3026-11df-85fd-d0968b3d1792	rollback-commit	0
bed11c30-3026-11df-85fd-d0968b3d1792		0
bed53ae0-3026-11df-85fd-d0968b3d1792		0
beda43f0-3026-11df-85fd-d0968b3d1792		0
bedded70-3026-11df-85fd-d0968b3d1792		0
bee16fe0-3026-11df-85fd-d0968b3d1792		0
c1ee13a0-3026-11df-85fd-d0968b3d1792		0
c1f3e000-3026-11df-85fd-d0968b3d1792		0
c1f9ac60-3026-11df-85fd-d0968b3d1792		0
c1fe1930-3026-11df-85fd-d0968b3d1792	CONFIGURE_INTERFACES	0
c2032240-3026-11df-85fd-d0968b3d1792	DELETE_LOGICAL_INTERFACE	0
c208c790-3026-11df-85fd-d0968b3d1792	REFRESH	0
c20ebb00-3026-11df-85fd-d0968b3d1792	prepare-commit	0
c2143940-3026-11df-85fd-d0968b3d1792	confirm-commit	0
c21a05a0-3026-11df-85fd-d0968b3d1792	rollback-commit	0
c21d6100-3026-11df-85fd-d0968b3d1792		0
c220bc60-3026-11df-85fd-d0968b3d1792		0
c225c570-3026-11df-85fd-d0968b3d1792		0
c2296ef0-3026-11df-85fd-d0968b3d1792		0
c22cf160-3026-11df-85fd-d0968b3d1792		0
c568bb70-3026-11df-85fd-d0968b3d1792		0
c56d9d70-3026-11df-85fd-d0968b3d1792		0
c5719510-3026-11df-85fd-d0968b3d1792		0
c576ec40-3026-11df-85fd-d0968b3d1792	CONFIGURE_INTERFACES	0
c57ae3e0-3026-11df-85fd-d0968b3d1792	DELETE_LOGICAL_INTERFACE	0
c57f29a0-3026-11df-85fd-d0968b3d1792	REFRESH	0
c582fa30-3026-11df-85fd-d0968b3d1792	prepare-commit	0
c588c690-3026-11df-85fd-d0968b3d1792	confirm-commit	0
c58d8180-3026-11df-85fd-d0968b3d1792	rollback-commit	0
c590b5d0-3026-11df-85fd-d0968b3d1792		0
c59570c0-3026-11df-85fd-d0968b3d1792		0
c598cc20-3026-11df-85fd-d0968b3d1792		0
c59d38f0-3026-11df-85fd-d0968b3d1792		0
c5a1f3e0-3026-11df-85fd-d0968b3d1792		0
c88f76e0-3026-11df-85fd-d0968b3d1792		0
c89431d0-3026-11df-85fd-d0968b3d1792		0
c8976620-3026-11df-85fd-d0968b3d1792		0
c89d5990-3026-11df-85fd-d0968b3d1792	CONFIGURE_INTERFACES	0
c8a10310-3026-11df-85fd-d0968b3d1792	DELETE_LOGICAL_INTERFACE	0
c8a4ac90-3026-11df-85fd-d0968b3d1792	REFRESH	0
c8a94070-3026-11df-85fd-d0968b3d1792	prepare-commit	0
c8ad3810-3026-11df-85fd-d0968b3d1792	confirm-commit	0
c8b108a0-3026-11df-85fd-d0968b3d1792	rollback-commit	0
c8b43cf0-3026-11df-85fd-d0968b3d1792		0
c8b8a9c0-3026-11df-85fd-d0968b3d1792		0
c8bc0520-3026-11df-85fd-d0968b3d1792		0
c8bffcc0-3026-11df-85fd-d0968b3d1792		0
c8c37f30-3026-11df-85fd-d0968b3d1792		0
cb9c8fd0-3026-11df-85fd-d0968b3d1792		0
cba20e10-3026-11df-85fd-d0968b3d1792		0
cba59080-3026-11df-85fd-d0968b3d1792		0
cbaa7280-3026-11df-85fd-d0968b3d1792	CONFIGURE_INTERFACES	0
cbae9130-3026-11df-85fd-d0968b3d1792	DELETE_LOGICAL_INTERFACE	0
cbb2fe00-3026-11df-85fd-d0968b3d1792	REFRESH	0
cbb6f5a0-3026-11df-85fd-d0968b3d1792	prepare-commit	0
cbbd3730-3026-11df-85fd-d0968b3d1792	confirm-commit	0
cbc28e60-3026-11df-85fd-d0968b3d1792	rollback-commit	0
cbc637e0-3026-11df-85fd-d0968b3d1792		0
cbcb6800-3026-11df-85fd-d0968b3d1792		0
cbd37e50-3026-11df-85fd-d0968b3d1792		0
cbd83940-3026-11df-85fd-d0968b3d1792		0
cbde53c0-3026-11df-85fd-d0968b3d1792		0
ced6ac30-3026-11df-85fd-d0968b3d1792		0
cedd3be0-3026-11df-85fd-d0968b3d1792		0
cee37d70-3026-11df-85fd-d0968b3d1792		0
ceea3430-3026-11df-85fd-d0968b3d1792	CONFIGURE_INTERFACES	0
ceee2bd0-3026-11df-85fd-d0968b3d1792	DELETE_LOGICAL_INTERFACE	0
cef27190-3026-11df-85fd-d0968b3d1792	REFRESH	0
cef83df0-3026-11df-85fd-d0968b3d1792	prepare-commit	0
cefd4700-3026-11df-85fd-d0968b3d1792	confirm-commit	0
cf0165b0-3026-11df-85fd-d0968b3d1792	rollback-commit	0
cf04e820-3026-11df-85fd-d0968b3d1792		0
cf09ca20-3026-11df-85fd-d0968b3d1792		0
cf0e0fe0-3026-11df-85fd-d0968b3d1792		0
cf12a3c0-3026-11df-85fd-d0968b3d1792		0
cf15ff20-3026-11df-85fd-d0968b3d1792		0
d2100540-3026-11df-85fd-d0968b3d1792		0
d2149920-3026-11df-85fd-d0968b3d1792		0
d21905f0-3026-11df-85fd-d0968b3d1792		0
d2214350-3026-11df-85fd-d0968b3d1792	CONFIGURE_INTERFACES	0
d225b020-3026-11df-85fd-d0968b3d1792	DELETE_LOGICAL_INTERFACE	0
d22a4400-3026-11df-85fd-d0968b3d1792	REFRESH	0
d22e62b0-3026-11df-85fd-d0968b3d1792	prepare-commit	0
d233b9e0-3026-11df-85fd-d0968b3d1792	confirm-commit	0
d237b180-3026-11df-85fd-d0968b3d1792	rollback-commit	0
d23b33f0-3026-11df-85fd-d0968b3d1792		0
d23edd70-3026-11df-85fd-d0968b3d1792		0
d26ea000-3026-11df-85fd-d0968b3d1792		0
d2730cd0-3026-11df-85fd-d0968b3d1792		0
d276b650-3026-11df-85fd-d0968b3d1792		0
d5724310-3026-11df-85fd-d0968b3d1792		0
d5777330-3026-11df-85fd-d0968b3d1792		0
d57c0710-3026-11df-85fd-d0968b3d1792		0
d581fa80-3026-11df-85fd-d0968b3d1792	CONFIGURE_INTERFACES	0
d5883c10-3026-11df-85fd-d0968b3d1792	DELETE_LOGICAL_INTERFACE	0
d58ca8e0-3026-11df-85fd-d0968b3d1792	REFRESH	0
d590a080-3026-11df-85fd-d0968b3d1792	prepare-commit	0
d5955b70-3026-11df-85fd-d0968b3d1792	confirm-commit	0
d59b27d0-3026-11df-85fd-d0968b3d1792	rollback-commit	0
d59f6d90-3026-11df-85fd-d0968b3d1792		0
d5a512e0-3026-11df-85fd-d0968b3d1792		0
d5a8e370-3026-11df-85fd-d0968b3d1792		0
d5ae61b0-3026-11df-85fd-d0968b3d1792		0
d5b31ca0-3026-11df-85fd-d0968b3d1792		0
d8c47b50-3026-11df-85fd-d0968b3d1792		0
d8cbf560-3026-11df-85fd-d0968b3d1792		0
d8cfc5f0-3026-11df-85fd-d0968b3d1792		0
d8d4cf00-3026-11df-85fd-d0968b3d1792	CONFIGURE_INTERFACES	0
d8d9b100-3026-11df-85fd-d0968b3d1792	DELETE_LOGICAL_INTERFACE	0
d8dfa470-3026-11df-85fd-d0968b3d1792	REFRESH	0
d8e39c10-3026-11df-85fd-d0968b3d1792	prepare-commit	0
d8e8cc30-3026-11df-85fd-d0968b3d1792	confirm-commit	0
d8edfc50-3026-11df-85fd-d0968b3d1792	rollback-commit	0
d8f2de50-3026-11df-85fd-d0968b3d1792		0
d8f639b0-3026-11df-85fd-d0968b3d1792		0
d8f9e330-3026-11df-85fd-d0968b3d1792		0
d8fd8cb0-3026-11df-85fd-d0968b3d1792		0
d90295c0-3026-11df-85fd-d0968b3d1792		0
dc292a20-3026-11df-85fd-d0968b3d1792		0
dc2e5a40-3026-11df-85fd-d0968b3d1792		0
dc349bd0-3026-11df-85fd-d0968b3d1792		0
dc3add60-3026-11df-85fd-d0968b3d1792	CONFIGURE_INTERFACES	0
dc3f9850-3026-11df-85fd-d0968b3d1792	DELETE_LOGICAL_INTERFACE	0
dc43de10-3026-11df-85fd-d0968b3d1792	REFRESH	0
dc48c010-3026-11df-85fd-d0968b3d1792	prepare-commit	0
dc4eb380-3026-11df-85fd-d0968b3d1792	confirm-commit	0
dc534760-3026-11df-85fd-d0968b3d1792	rollback-commit	0
dc56c9d0-3026-11df-85fd-d0968b3d1792		0
dc5a4c40-3026-11df-85fd-d0968b3d1792		0
dc5f0730-3026-11df-85fd-d0968b3d1792		0
dc62fed0-3026-11df-85fd-d0968b3d1792		0
dc671d80-3026-11df-85fd-d0968b3d1792		0
df671710-3026-11df-85fd-d0968b3d1792		0
df6bd200-3026-11df-85fd-d0968b3d1792		0
df710220-3026-11df-85fd-d0968b3d1792		0
df76f590-3026-11df-85fd-d0968b3d1792	CONFIGURE_INTERFACES	0
df7bfea0-3026-11df-85fd-d0968b3d1792	DELETE_LOGICAL_INTERFACE	0
df806b70-3026-11df-85fd-d0968b3d1792	REFRESH	0
df84b130-3026-11df-85fd-d0968b3d1792	prepare-commit	0
df894510-3026-11df-85fd-d0968b3d1792	confirm-commit	0
df8f5f90-3026-11df-85fd-d0968b3d1792	rollback-commit	0
df930910-3026-11df-85fd-d0968b3d1792		0
df96b290-3026-11df-85fd-d0968b3d1792		0
df9a3500-3026-11df-85fd-d0968b3d1792		0
df9fda50-3026-11df-85fd-d0968b3d1792		0
dfa35cc0-3026-11df-85fd-d0968b3d1792		0
18f484e0-3027-11df-85fd-d0968b3d1792		0
18f9b500-3027-11df-85fd-d0968b3d1792		0
19006bc0-3027-11df-85fd-d0968b3d1792		0
1904b180-3027-11df-85fd-d0968b3d1792	refresh	0
19099380-3027-11df-85fd-d0968b3d1792	router-action	0
190eeab0-3027-11df-85fd-d0968b3d1792	dummy	0
19133070-3027-11df-85fd-d0968b3d1792		0
1916b2e0-3027-11df-85fd-d0968b3d1792		0
191a3550-3027-11df-85fd-d0968b3d1792		0
191e2cf0-3027-11df-85fd-d0968b3d1792		0
19235d10-3027-11df-85fd-d0968b3d1792		0
19277bc0-3027-11df-85fd-d0968b3d1792		0
192b7360-3027-11df-85fd-d0968b3d1792		0
192ef5d0-3027-11df-85fd-d0968b3d1792		0
19331480-3027-11df-85fd-d0968b3d1792		0
1938e0e0-3027-11df-85fd-d0968b3d1792		0
193efb60-3027-11df-85fd-d0968b3d1792		0
1942f300-3027-11df-85fd-d0968b3d1792		0
\.


--
-- Data for Name: policies_organizationsallowed; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY policies_organizationsallowed (policy_key, organizations_allowed, "position") FROM stdin;
9516d930-3025-11df-bd6b-a6bcc20293a6	ITI	0
9524bbe0-3025-11df-bd6b-a6bcc20293a6	ITI	0
9527c920-3025-11df-bd6b-a6bcc20293a6	ITI	0
6b3489e0-3026-11df-85fd-d0968b3d1792	ITI	0
6b3a0820-3026-11df-85fd-d0968b3d1792	ITI	0
6b4293a0-3026-11df-85fd-d0968b3d1792	ITI	0
6b4aa9f0-3026-11df-85fd-d0968b3d1792	ITI	0
6b5187c0-3026-11df-85fd-d0968b3d1792	ITI	0
6b583e80-3026-11df-85fd-d0968b3d1792	ITI	0
93174330-3026-11df-85fd-d0968b3d1792	ITI	0
931fceb0-3026-11df-85fd-d0968b3d1792	ITI	0
9326ac80-3026-11df-85fd-d0968b3d1792	ITI	0
932c03b0-3026-11df-85fd-d0968b3d1792	ITI	0
93324540-3026-11df-85fd-d0968b3d1792	ITI	0
933811a0-3026-11df-85fd-d0968b3d1792	ITI	0
b49a8440-3026-11df-85fd-d0968b3d1792	ITI	0
b49fdb70-3026-11df-85fd-d0968b3d1792	ITI	0
b4a75580-3026-11df-85fd-d0968b3d1792	ITI	0
b4ac3780-3026-11df-85fd-d0968b3d1792	ITI	0
b4b11980-3026-11df-85fd-d0968b3d1792	ITI	0
b4b697c0-3026-11df-85fd-d0968b3d1792	ITI	0
b4bc1600-3026-11df-85fd-d0968b3d1792	ITI	0
b4c2ccc0-3026-11df-85fd-d0968b3d1792	ITI	0
b4c8c030-3026-11df-85fd-d0968b3d1792	ITI	0
b4cdc940-3026-11df-85fd-d0968b3d1792	ITI	0
b4d2d250-3026-11df-85fd-d0968b3d1792	ITI	0
b4d67bd0-3026-11df-85fd-d0968b3d1792	ITI	0
b4dae8a0-3026-11df-85fd-d0968b3d1792	ITI	0
b4deb930-3026-11df-85fd-d0968b3d1792	ITI	0
b7e47f20-3026-11df-85fd-d0968b3d1792	ITI	0
b7eac0b0-3026-11df-85fd-d0968b3d1792	ITI	0
b7f0b420-3026-11df-85fd-d0968b3d1792	ITI	0
b7f59620-3026-11df-85fd-d0968b3d1792	ITI	0
b80a0880-3026-11df-85fd-d0968b3d1792	ITI	0
b8115b80-3026-11df-85fd-d0968b3d1792	ITI	0
b8157a30-3026-11df-85fd-d0968b3d1792	ITI	0
b81b6da0-3026-11df-85fd-d0968b3d1792	ITI	0
b821af30-3026-11df-85fd-d0968b3d1792	ITI	0
b82531a0-3026-11df-85fd-d0968b3d1792	ITI	0
b82ad6f0-3026-11df-85fd-d0968b3d1792	ITI	0
b82f1cb0-3026-11df-85fd-d0968b3d1792	ITI	0
b832ed40-3026-11df-85fd-d0968b3d1792	ITI	0
b83696c0-3026-11df-85fd-d0968b3d1792	ITI	0
bb5c19b0-3026-11df-85fd-d0968b3d1792	ITI	0
bb608680-3026-11df-85fd-d0968b3d1792	ITI	0
bb662bd0-3026-11df-85fd-d0968b3d1792	ITI	0
bb6a7190-3026-11df-85fd-d0968b3d1792	ITI	0
bb6f7aa0-3026-11df-85fd-d0968b3d1792	ITI	0
bb75bc30-3026-11df-85fd-d0968b3d1792	ITI	0
bb79b3d0-3026-11df-85fd-d0968b3d1792	ITI	0
bb7ebce0-3026-11df-85fd-d0968b3d1792	ITI	0
bb8573a0-3026-11df-85fd-d0968b3d1792	ITI	0
bb8a0780-3026-11df-85fd-d0968b3d1792	ITI	0
bb8d89f0-3026-11df-85fd-d0968b3d1792	ITI	0
bb913370-3026-11df-85fd-d0968b3d1792	ITI	0
bb96b1b0-3026-11df-85fd-d0968b3d1792	ITI	0
bb9a8240-3026-11df-85fd-d0968b3d1792	ITI	0
bea689c0-3026-11df-85fd-d0968b3d1792	ITI	0
bead1970-3026-11df-85fd-d0968b3d1792	ITI	0
beb13820-3026-11df-85fd-d0968b3d1792	ITI	0
beb556d0-3026-11df-85fd-d0968b3d1792	ITI	0
beba5fe0-3026-11df-85fd-d0968b3d1792	ITI	0
bebf41e0-3026-11df-85fd-d0968b3d1792	ITI	0
bec33980-3026-11df-85fd-d0968b3d1792	ITI	0
bec92cf0-3026-11df-85fd-d0968b3d1792	ITI	0
becd4ba0-3026-11df-85fd-d0968b3d1792	ITI	0
bed11c30-3026-11df-85fd-d0968b3d1792	ITI	0
bed53ae0-3026-11df-85fd-d0968b3d1792	ITI	0
beda43f0-3026-11df-85fd-d0968b3d1792	ITI	0
bedded70-3026-11df-85fd-d0968b3d1792	ITI	0
bee16fe0-3026-11df-85fd-d0968b3d1792	ITI	0
c1ee13a0-3026-11df-85fd-d0968b3d1792	ITI	0
c1f3e000-3026-11df-85fd-d0968b3d1792	ITI	0
c1f9ac60-3026-11df-85fd-d0968b3d1792	ITI	0
c1fe1930-3026-11df-85fd-d0968b3d1792	ITI	0
c2032240-3026-11df-85fd-d0968b3d1792	ITI	0
c208c790-3026-11df-85fd-d0968b3d1792	ITI	0
c20ebb00-3026-11df-85fd-d0968b3d1792	ITI	0
c2143940-3026-11df-85fd-d0968b3d1792	ITI	0
c21a05a0-3026-11df-85fd-d0968b3d1792	ITI	0
c21d6100-3026-11df-85fd-d0968b3d1792	ITI	0
c220bc60-3026-11df-85fd-d0968b3d1792	ITI	0
c225c570-3026-11df-85fd-d0968b3d1792	ITI	0
c2296ef0-3026-11df-85fd-d0968b3d1792	ITI	0
c22cf160-3026-11df-85fd-d0968b3d1792	ITI	0
c568bb70-3026-11df-85fd-d0968b3d1792	ITI	0
c56d9d70-3026-11df-85fd-d0968b3d1792	ITI	0
c5719510-3026-11df-85fd-d0968b3d1792	ITI	0
c576ec40-3026-11df-85fd-d0968b3d1792	ITI	0
c57ae3e0-3026-11df-85fd-d0968b3d1792	ITI	0
c57f29a0-3026-11df-85fd-d0968b3d1792	ITI	0
c582fa30-3026-11df-85fd-d0968b3d1792	ITI	0
c588c690-3026-11df-85fd-d0968b3d1792	ITI	0
c58d8180-3026-11df-85fd-d0968b3d1792	ITI	0
c590b5d0-3026-11df-85fd-d0968b3d1792	ITI	0
c59570c0-3026-11df-85fd-d0968b3d1792	ITI	0
c598cc20-3026-11df-85fd-d0968b3d1792	ITI	0
c59d38f0-3026-11df-85fd-d0968b3d1792	ITI	0
c5a1f3e0-3026-11df-85fd-d0968b3d1792	ITI	0
c88f76e0-3026-11df-85fd-d0968b3d1792	ITI	0
c89431d0-3026-11df-85fd-d0968b3d1792	ITI	0
c8976620-3026-11df-85fd-d0968b3d1792	ITI	0
c89d5990-3026-11df-85fd-d0968b3d1792	ITI	0
c8a10310-3026-11df-85fd-d0968b3d1792	ITI	0
c8a4ac90-3026-11df-85fd-d0968b3d1792	ITI	0
c8a94070-3026-11df-85fd-d0968b3d1792	ITI	0
c8ad3810-3026-11df-85fd-d0968b3d1792	ITI	0
c8b108a0-3026-11df-85fd-d0968b3d1792	ITI	0
c8b43cf0-3026-11df-85fd-d0968b3d1792	ITI	0
c8b8a9c0-3026-11df-85fd-d0968b3d1792	ITI	0
c8bc0520-3026-11df-85fd-d0968b3d1792	ITI	0
c8bffcc0-3026-11df-85fd-d0968b3d1792	ITI	0
c8c37f30-3026-11df-85fd-d0968b3d1792	ITI	0
cb9c8fd0-3026-11df-85fd-d0968b3d1792	ITI	0
cba20e10-3026-11df-85fd-d0968b3d1792	ITI	0
cba59080-3026-11df-85fd-d0968b3d1792	ITI	0
cbaa7280-3026-11df-85fd-d0968b3d1792	ITI	0
cbae9130-3026-11df-85fd-d0968b3d1792	ITI	0
cbb2fe00-3026-11df-85fd-d0968b3d1792	ITI	0
cbb6f5a0-3026-11df-85fd-d0968b3d1792	ITI	0
cbbd3730-3026-11df-85fd-d0968b3d1792	ITI	0
cbc28e60-3026-11df-85fd-d0968b3d1792	ITI	0
cbc637e0-3026-11df-85fd-d0968b3d1792	ITI	0
cbcb6800-3026-11df-85fd-d0968b3d1792	ITI	0
cbd37e50-3026-11df-85fd-d0968b3d1792	ITI	0
cbd83940-3026-11df-85fd-d0968b3d1792	ITI	0
cbde53c0-3026-11df-85fd-d0968b3d1792	ITI	0
ced6ac30-3026-11df-85fd-d0968b3d1792	ITI	0
cedd3be0-3026-11df-85fd-d0968b3d1792	ITI	0
cee37d70-3026-11df-85fd-d0968b3d1792	ITI	0
ceea3430-3026-11df-85fd-d0968b3d1792	ITI	0
ceee2bd0-3026-11df-85fd-d0968b3d1792	ITI	0
cef27190-3026-11df-85fd-d0968b3d1792	ITI	0
cef83df0-3026-11df-85fd-d0968b3d1792	ITI	0
cefd4700-3026-11df-85fd-d0968b3d1792	ITI	0
cf0165b0-3026-11df-85fd-d0968b3d1792	ITI	0
cf04e820-3026-11df-85fd-d0968b3d1792	ITI	0
cf09ca20-3026-11df-85fd-d0968b3d1792	ITI	0
cf0e0fe0-3026-11df-85fd-d0968b3d1792	ITI	0
cf12a3c0-3026-11df-85fd-d0968b3d1792	ITI	0
cf15ff20-3026-11df-85fd-d0968b3d1792	ITI	0
d2100540-3026-11df-85fd-d0968b3d1792	ITI	0
d2149920-3026-11df-85fd-d0968b3d1792	ITI	0
d21905f0-3026-11df-85fd-d0968b3d1792	ITI	0
d2214350-3026-11df-85fd-d0968b3d1792	ITI	0
d225b020-3026-11df-85fd-d0968b3d1792	ITI	0
d22a4400-3026-11df-85fd-d0968b3d1792	ITI	0
d22e62b0-3026-11df-85fd-d0968b3d1792	ITI	0
d233b9e0-3026-11df-85fd-d0968b3d1792	ITI	0
d237b180-3026-11df-85fd-d0968b3d1792	ITI	0
d23b33f0-3026-11df-85fd-d0968b3d1792	ITI	0
d23edd70-3026-11df-85fd-d0968b3d1792	ITI	0
d26ea000-3026-11df-85fd-d0968b3d1792	ITI	0
d2730cd0-3026-11df-85fd-d0968b3d1792	ITI	0
d276b650-3026-11df-85fd-d0968b3d1792	ITI	0
d5724310-3026-11df-85fd-d0968b3d1792	ITI	0
d5777330-3026-11df-85fd-d0968b3d1792	ITI	0
d57c0710-3026-11df-85fd-d0968b3d1792	ITI	0
d581fa80-3026-11df-85fd-d0968b3d1792	ITI	0
d5883c10-3026-11df-85fd-d0968b3d1792	ITI	0
d58ca8e0-3026-11df-85fd-d0968b3d1792	ITI	0
d590a080-3026-11df-85fd-d0968b3d1792	ITI	0
d5955b70-3026-11df-85fd-d0968b3d1792	ITI	0
d59b27d0-3026-11df-85fd-d0968b3d1792	ITI	0
d59f6d90-3026-11df-85fd-d0968b3d1792	ITI	0
d5a512e0-3026-11df-85fd-d0968b3d1792	ITI	0
d5a8e370-3026-11df-85fd-d0968b3d1792	ITI	0
d5ae61b0-3026-11df-85fd-d0968b3d1792	ITI	0
d5b31ca0-3026-11df-85fd-d0968b3d1792	ITI	0
d8c47b50-3026-11df-85fd-d0968b3d1792	ITI	0
d8cbf560-3026-11df-85fd-d0968b3d1792	ITI	0
d8cfc5f0-3026-11df-85fd-d0968b3d1792	ITI	0
d8d4cf00-3026-11df-85fd-d0968b3d1792	ITI	0
d8d9b100-3026-11df-85fd-d0968b3d1792	ITI	0
d8dfa470-3026-11df-85fd-d0968b3d1792	ITI	0
d8e39c10-3026-11df-85fd-d0968b3d1792	ITI	0
d8e8cc30-3026-11df-85fd-d0968b3d1792	ITI	0
d8edfc50-3026-11df-85fd-d0968b3d1792	ITI	0
d8f2de50-3026-11df-85fd-d0968b3d1792	ITI	0
d8f639b0-3026-11df-85fd-d0968b3d1792	ITI	0
d8f9e330-3026-11df-85fd-d0968b3d1792	ITI	0
d8fd8cb0-3026-11df-85fd-d0968b3d1792	ITI	0
d90295c0-3026-11df-85fd-d0968b3d1792	ITI	0
dc292a20-3026-11df-85fd-d0968b3d1792	ITI	0
dc2e5a40-3026-11df-85fd-d0968b3d1792	ITI	0
dc349bd0-3026-11df-85fd-d0968b3d1792	ITI	0
dc3add60-3026-11df-85fd-d0968b3d1792	ITI	0
dc3f9850-3026-11df-85fd-d0968b3d1792	ITI	0
dc43de10-3026-11df-85fd-d0968b3d1792	ITI	0
dc48c010-3026-11df-85fd-d0968b3d1792	ITI	0
dc4eb380-3026-11df-85fd-d0968b3d1792	ITI	0
dc534760-3026-11df-85fd-d0968b3d1792	ITI	0
dc56c9d0-3026-11df-85fd-d0968b3d1792	ITI	0
dc5a4c40-3026-11df-85fd-d0968b3d1792	ITI	0
dc5f0730-3026-11df-85fd-d0968b3d1792	ITI	0
dc62fed0-3026-11df-85fd-d0968b3d1792	ITI	0
dc671d80-3026-11df-85fd-d0968b3d1792	ITI	0
df671710-3026-11df-85fd-d0968b3d1792	ITI	0
df6bd200-3026-11df-85fd-d0968b3d1792	ITI	0
df710220-3026-11df-85fd-d0968b3d1792	ITI	0
df76f590-3026-11df-85fd-d0968b3d1792	ITI	0
df7bfea0-3026-11df-85fd-d0968b3d1792	ITI	0
df806b70-3026-11df-85fd-d0968b3d1792	ITI	0
df84b130-3026-11df-85fd-d0968b3d1792	ITI	0
df894510-3026-11df-85fd-d0968b3d1792	ITI	0
df8f5f90-3026-11df-85fd-d0968b3d1792	ITI	0
df930910-3026-11df-85fd-d0968b3d1792	ITI	0
df96b290-3026-11df-85fd-d0968b3d1792	ITI	0
df9a3500-3026-11df-85fd-d0968b3d1792	ITI	0
df9fda50-3026-11df-85fd-d0968b3d1792	ITI	0
dfa35cc0-3026-11df-85fd-d0968b3d1792	ITI	0
18f484e0-3027-11df-85fd-d0968b3d1792	ITI	0
18f9b500-3027-11df-85fd-d0968b3d1792	ITI	0
19006bc0-3027-11df-85fd-d0968b3d1792	ITI	0
1904b180-3027-11df-85fd-d0968b3d1792	ITI	0
19099380-3027-11df-85fd-d0968b3d1792	ITI	0
190eeab0-3027-11df-85fd-d0968b3d1792	ITI	0
19133070-3027-11df-85fd-d0968b3d1792	ITI	0
1916b2e0-3027-11df-85fd-d0968b3d1792	ITI	0
191a3550-3027-11df-85fd-d0968b3d1792	ITI	0
191e2cf0-3027-11df-85fd-d0968b3d1792	ITI	0
19235d10-3027-11df-85fd-d0968b3d1792	ITI	0
19277bc0-3027-11df-85fd-d0968b3d1792	ITI	0
192b7360-3027-11df-85fd-d0968b3d1792	ITI	0
192ef5d0-3027-11df-85fd-d0968b3d1792	ITI	0
19331480-3027-11df-85fd-d0968b3d1792	ITI	0
1938e0e0-3027-11df-85fd-d0968b3d1792	ITI	0
193efb60-3027-11df-85fd-d0968b3d1792	ITI	0
1942f300-3027-11df-85fd-d0968b3d1792	ITI	0
\.


--
-- Data for Name: policies_usersallowed; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY policies_usersallowed (policy_key, users_allowed, "position") FROM stdin;
9516d930-3025-11df-bd6b-a6bcc20293a6		0
9524bbe0-3025-11df-bd6b-a6bcc20293a6		0
9527c920-3025-11df-bd6b-a6bcc20293a6		0
6b3489e0-3026-11df-85fd-d0968b3d1792	i2cat	0
6b3a0820-3026-11df-85fd-d0968b3d1792	i2cat	0
6b4293a0-3026-11df-85fd-d0968b3d1792	i2cat	0
6b4aa9f0-3026-11df-85fd-d0968b3d1792	i2cat	0
6b5187c0-3026-11df-85fd-d0968b3d1792	i2cat	0
6b583e80-3026-11df-85fd-d0968b3d1792	i2cat	0
93174330-3026-11df-85fd-d0968b3d1792	i2cat-user	0
93174330-3026-11df-85fd-d0968b3d1792	i2cat	1
931fceb0-3026-11df-85fd-d0968b3d1792	i2cat-user	0
931fceb0-3026-11df-85fd-d0968b3d1792	i2cat	1
9326ac80-3026-11df-85fd-d0968b3d1792	i2cat-user	0
9326ac80-3026-11df-85fd-d0968b3d1792	i2cat	1
932c03b0-3026-11df-85fd-d0968b3d1792	i2cat-user	0
932c03b0-3026-11df-85fd-d0968b3d1792	i2cat	1
93324540-3026-11df-85fd-d0968b3d1792	i2cat-user	0
93324540-3026-11df-85fd-d0968b3d1792	i2cat	1
933811a0-3026-11df-85fd-d0968b3d1792	i2cat-user	0
933811a0-3026-11df-85fd-d0968b3d1792	i2cat	1
b49a8440-3026-11df-85fd-d0968b3d1792	i2cat	0
b49fdb70-3026-11df-85fd-d0968b3d1792	i2cat	0
b4a75580-3026-11df-85fd-d0968b3d1792	i2cat	0
b4ac3780-3026-11df-85fd-d0968b3d1792	i2cat	0
b4b11980-3026-11df-85fd-d0968b3d1792	i2cat	0
b4b697c0-3026-11df-85fd-d0968b3d1792	i2cat	0
b4bc1600-3026-11df-85fd-d0968b3d1792	i2cat	0
b4c2ccc0-3026-11df-85fd-d0968b3d1792	i2cat	0
b4c8c030-3026-11df-85fd-d0968b3d1792	i2cat	0
b4cdc940-3026-11df-85fd-d0968b3d1792	i2cat	0
b4d2d250-3026-11df-85fd-d0968b3d1792	i2cat	0
b4d67bd0-3026-11df-85fd-d0968b3d1792	i2cat	0
b4dae8a0-3026-11df-85fd-d0968b3d1792	i2cat	0
b4deb930-3026-11df-85fd-d0968b3d1792	i2cat	0
b7e47f20-3026-11df-85fd-d0968b3d1792	i2cat	0
b7eac0b0-3026-11df-85fd-d0968b3d1792	i2cat	0
b7f0b420-3026-11df-85fd-d0968b3d1792	i2cat	0
b7f59620-3026-11df-85fd-d0968b3d1792	i2cat	0
b80a0880-3026-11df-85fd-d0968b3d1792	i2cat	0
b8115b80-3026-11df-85fd-d0968b3d1792	i2cat	0
b8157a30-3026-11df-85fd-d0968b3d1792	i2cat	0
b81b6da0-3026-11df-85fd-d0968b3d1792	i2cat	0
b821af30-3026-11df-85fd-d0968b3d1792	i2cat	0
b82531a0-3026-11df-85fd-d0968b3d1792	i2cat	0
b82ad6f0-3026-11df-85fd-d0968b3d1792	i2cat	0
b82f1cb0-3026-11df-85fd-d0968b3d1792	i2cat	0
b832ed40-3026-11df-85fd-d0968b3d1792	i2cat	0
b83696c0-3026-11df-85fd-d0968b3d1792	i2cat	0
bb5c19b0-3026-11df-85fd-d0968b3d1792	i2cat	0
bb608680-3026-11df-85fd-d0968b3d1792	i2cat	0
bb662bd0-3026-11df-85fd-d0968b3d1792	i2cat	0
bb6a7190-3026-11df-85fd-d0968b3d1792	i2cat	0
bb6f7aa0-3026-11df-85fd-d0968b3d1792	i2cat	0
bb75bc30-3026-11df-85fd-d0968b3d1792	i2cat	0
bb79b3d0-3026-11df-85fd-d0968b3d1792	i2cat	0
bb7ebce0-3026-11df-85fd-d0968b3d1792	i2cat	0
bb8573a0-3026-11df-85fd-d0968b3d1792	i2cat	0
bb8a0780-3026-11df-85fd-d0968b3d1792	i2cat	0
bb8d89f0-3026-11df-85fd-d0968b3d1792	i2cat	0
bb913370-3026-11df-85fd-d0968b3d1792	i2cat	0
bb96b1b0-3026-11df-85fd-d0968b3d1792	i2cat	0
bb9a8240-3026-11df-85fd-d0968b3d1792	i2cat	0
bea689c0-3026-11df-85fd-d0968b3d1792	i2cat	0
bead1970-3026-11df-85fd-d0968b3d1792	i2cat	0
beb13820-3026-11df-85fd-d0968b3d1792	i2cat	0
beb556d0-3026-11df-85fd-d0968b3d1792	i2cat	0
beba5fe0-3026-11df-85fd-d0968b3d1792	i2cat	0
bebf41e0-3026-11df-85fd-d0968b3d1792	i2cat	0
bec33980-3026-11df-85fd-d0968b3d1792	i2cat	0
bec92cf0-3026-11df-85fd-d0968b3d1792	i2cat	0
becd4ba0-3026-11df-85fd-d0968b3d1792	i2cat	0
bed11c30-3026-11df-85fd-d0968b3d1792	i2cat	0
bed53ae0-3026-11df-85fd-d0968b3d1792	i2cat	0
beda43f0-3026-11df-85fd-d0968b3d1792	i2cat	0
bedded70-3026-11df-85fd-d0968b3d1792	i2cat	0
bee16fe0-3026-11df-85fd-d0968b3d1792	i2cat	0
c1ee13a0-3026-11df-85fd-d0968b3d1792	i2cat	0
c1f3e000-3026-11df-85fd-d0968b3d1792	i2cat	0
c1f9ac60-3026-11df-85fd-d0968b3d1792	i2cat	0
c1fe1930-3026-11df-85fd-d0968b3d1792	i2cat	0
c2032240-3026-11df-85fd-d0968b3d1792	i2cat	0
c208c790-3026-11df-85fd-d0968b3d1792	i2cat	0
c20ebb00-3026-11df-85fd-d0968b3d1792	i2cat	0
c2143940-3026-11df-85fd-d0968b3d1792	i2cat	0
c21a05a0-3026-11df-85fd-d0968b3d1792	i2cat	0
c21d6100-3026-11df-85fd-d0968b3d1792	i2cat	0
c220bc60-3026-11df-85fd-d0968b3d1792	i2cat	0
c225c570-3026-11df-85fd-d0968b3d1792	i2cat	0
c2296ef0-3026-11df-85fd-d0968b3d1792	i2cat	0
c22cf160-3026-11df-85fd-d0968b3d1792	i2cat	0
c568bb70-3026-11df-85fd-d0968b3d1792	i2cat	0
c56d9d70-3026-11df-85fd-d0968b3d1792	i2cat	0
c5719510-3026-11df-85fd-d0968b3d1792	i2cat	0
c576ec40-3026-11df-85fd-d0968b3d1792	i2cat	0
c57ae3e0-3026-11df-85fd-d0968b3d1792	i2cat	0
c57f29a0-3026-11df-85fd-d0968b3d1792	i2cat	0
c582fa30-3026-11df-85fd-d0968b3d1792	i2cat	0
c588c690-3026-11df-85fd-d0968b3d1792	i2cat	0
c58d8180-3026-11df-85fd-d0968b3d1792	i2cat	0
c590b5d0-3026-11df-85fd-d0968b3d1792	i2cat	0
c59570c0-3026-11df-85fd-d0968b3d1792	i2cat	0
c598cc20-3026-11df-85fd-d0968b3d1792	i2cat	0
c59d38f0-3026-11df-85fd-d0968b3d1792	i2cat	0
c5a1f3e0-3026-11df-85fd-d0968b3d1792	i2cat	0
c88f76e0-3026-11df-85fd-d0968b3d1792	i2cat	0
c89431d0-3026-11df-85fd-d0968b3d1792	i2cat	0
c8976620-3026-11df-85fd-d0968b3d1792	i2cat	0
c89d5990-3026-11df-85fd-d0968b3d1792	i2cat	0
c8a10310-3026-11df-85fd-d0968b3d1792	i2cat	0
c8a4ac90-3026-11df-85fd-d0968b3d1792	i2cat	0
c8a94070-3026-11df-85fd-d0968b3d1792	i2cat	0
c8ad3810-3026-11df-85fd-d0968b3d1792	i2cat	0
c8b108a0-3026-11df-85fd-d0968b3d1792	i2cat	0
c8b43cf0-3026-11df-85fd-d0968b3d1792	i2cat	0
c8b8a9c0-3026-11df-85fd-d0968b3d1792	i2cat	0
c8bc0520-3026-11df-85fd-d0968b3d1792	i2cat	0
c8bffcc0-3026-11df-85fd-d0968b3d1792	i2cat	0
c8c37f30-3026-11df-85fd-d0968b3d1792	i2cat	0
cb9c8fd0-3026-11df-85fd-d0968b3d1792	i2cat	0
cba20e10-3026-11df-85fd-d0968b3d1792	i2cat	0
cba59080-3026-11df-85fd-d0968b3d1792	i2cat	0
cbaa7280-3026-11df-85fd-d0968b3d1792	i2cat	0
cbae9130-3026-11df-85fd-d0968b3d1792	i2cat	0
cbb2fe00-3026-11df-85fd-d0968b3d1792	i2cat	0
cbb6f5a0-3026-11df-85fd-d0968b3d1792	i2cat	0
cbbd3730-3026-11df-85fd-d0968b3d1792	i2cat	0
cbc28e60-3026-11df-85fd-d0968b3d1792	i2cat	0
cbc637e0-3026-11df-85fd-d0968b3d1792	i2cat	0
cbcb6800-3026-11df-85fd-d0968b3d1792	i2cat	0
cbd37e50-3026-11df-85fd-d0968b3d1792	i2cat	0
cbd83940-3026-11df-85fd-d0968b3d1792	i2cat	0
cbde53c0-3026-11df-85fd-d0968b3d1792	i2cat	0
ced6ac30-3026-11df-85fd-d0968b3d1792	i2cat	0
cedd3be0-3026-11df-85fd-d0968b3d1792	i2cat	0
cee37d70-3026-11df-85fd-d0968b3d1792	i2cat	0
ceea3430-3026-11df-85fd-d0968b3d1792	i2cat	0
ceee2bd0-3026-11df-85fd-d0968b3d1792	i2cat	0
cef27190-3026-11df-85fd-d0968b3d1792	i2cat	0
cef83df0-3026-11df-85fd-d0968b3d1792	i2cat	0
cefd4700-3026-11df-85fd-d0968b3d1792	i2cat	0
cf0165b0-3026-11df-85fd-d0968b3d1792	i2cat	0
cf04e820-3026-11df-85fd-d0968b3d1792	i2cat	0
cf09ca20-3026-11df-85fd-d0968b3d1792	i2cat	0
cf0e0fe0-3026-11df-85fd-d0968b3d1792	i2cat	0
cf12a3c0-3026-11df-85fd-d0968b3d1792	i2cat	0
cf15ff20-3026-11df-85fd-d0968b3d1792	i2cat	0
d2100540-3026-11df-85fd-d0968b3d1792	i2cat	0
d2149920-3026-11df-85fd-d0968b3d1792	i2cat	0
d21905f0-3026-11df-85fd-d0968b3d1792	i2cat	0
d2214350-3026-11df-85fd-d0968b3d1792	i2cat	0
d225b020-3026-11df-85fd-d0968b3d1792	i2cat	0
d22a4400-3026-11df-85fd-d0968b3d1792	i2cat	0
d22e62b0-3026-11df-85fd-d0968b3d1792	i2cat	0
d233b9e0-3026-11df-85fd-d0968b3d1792	i2cat	0
d237b180-3026-11df-85fd-d0968b3d1792	i2cat	0
d23b33f0-3026-11df-85fd-d0968b3d1792	i2cat	0
d23edd70-3026-11df-85fd-d0968b3d1792	i2cat	0
d26ea000-3026-11df-85fd-d0968b3d1792	i2cat	0
d2730cd0-3026-11df-85fd-d0968b3d1792	i2cat	0
d276b650-3026-11df-85fd-d0968b3d1792	i2cat	0
d5724310-3026-11df-85fd-d0968b3d1792	i2cat	0
d5777330-3026-11df-85fd-d0968b3d1792	i2cat	0
d57c0710-3026-11df-85fd-d0968b3d1792	i2cat	0
d581fa80-3026-11df-85fd-d0968b3d1792	i2cat	0
d5883c10-3026-11df-85fd-d0968b3d1792	i2cat	0
d58ca8e0-3026-11df-85fd-d0968b3d1792	i2cat	0
d590a080-3026-11df-85fd-d0968b3d1792	i2cat	0
d5955b70-3026-11df-85fd-d0968b3d1792	i2cat	0
d59b27d0-3026-11df-85fd-d0968b3d1792	i2cat	0
d59f6d90-3026-11df-85fd-d0968b3d1792	i2cat	0
d5a512e0-3026-11df-85fd-d0968b3d1792	i2cat	0
d5a8e370-3026-11df-85fd-d0968b3d1792	i2cat	0
d5ae61b0-3026-11df-85fd-d0968b3d1792	i2cat	0
d5b31ca0-3026-11df-85fd-d0968b3d1792	i2cat	0
d8c47b50-3026-11df-85fd-d0968b3d1792	i2cat	0
d8cbf560-3026-11df-85fd-d0968b3d1792	i2cat	0
d8cfc5f0-3026-11df-85fd-d0968b3d1792	i2cat	0
d8d4cf00-3026-11df-85fd-d0968b3d1792	i2cat	0
d8d9b100-3026-11df-85fd-d0968b3d1792	i2cat	0
d8dfa470-3026-11df-85fd-d0968b3d1792	i2cat	0
d8e39c10-3026-11df-85fd-d0968b3d1792	i2cat	0
d8e8cc30-3026-11df-85fd-d0968b3d1792	i2cat	0
d8edfc50-3026-11df-85fd-d0968b3d1792	i2cat	0
d8f2de50-3026-11df-85fd-d0968b3d1792	i2cat	0
d8f639b0-3026-11df-85fd-d0968b3d1792	i2cat	0
d8f9e330-3026-11df-85fd-d0968b3d1792	i2cat	0
d8fd8cb0-3026-11df-85fd-d0968b3d1792	i2cat	0
d90295c0-3026-11df-85fd-d0968b3d1792	i2cat	0
dc292a20-3026-11df-85fd-d0968b3d1792	i2cat	0
dc2e5a40-3026-11df-85fd-d0968b3d1792	i2cat	0
dc349bd0-3026-11df-85fd-d0968b3d1792	i2cat	0
dc3add60-3026-11df-85fd-d0968b3d1792	i2cat	0
dc3f9850-3026-11df-85fd-d0968b3d1792	i2cat	0
dc43de10-3026-11df-85fd-d0968b3d1792	i2cat	0
dc48c010-3026-11df-85fd-d0968b3d1792	i2cat	0
dc4eb380-3026-11df-85fd-d0968b3d1792	i2cat	0
dc534760-3026-11df-85fd-d0968b3d1792	i2cat	0
dc56c9d0-3026-11df-85fd-d0968b3d1792	i2cat	0
dc5a4c40-3026-11df-85fd-d0968b3d1792	i2cat	0
dc5f0730-3026-11df-85fd-d0968b3d1792	i2cat	0
dc62fed0-3026-11df-85fd-d0968b3d1792	i2cat	0
dc671d80-3026-11df-85fd-d0968b3d1792	i2cat	0
df671710-3026-11df-85fd-d0968b3d1792	i2cat	0
df6bd200-3026-11df-85fd-d0968b3d1792	i2cat	0
df710220-3026-11df-85fd-d0968b3d1792	i2cat	0
df76f590-3026-11df-85fd-d0968b3d1792	i2cat	0
df7bfea0-3026-11df-85fd-d0968b3d1792	i2cat	0
df806b70-3026-11df-85fd-d0968b3d1792	i2cat	0
df84b130-3026-11df-85fd-d0968b3d1792	i2cat	0
df894510-3026-11df-85fd-d0968b3d1792	i2cat	0
df8f5f90-3026-11df-85fd-d0968b3d1792	i2cat	0
df930910-3026-11df-85fd-d0968b3d1792	i2cat	0
df96b290-3026-11df-85fd-d0968b3d1792	i2cat	0
df9a3500-3026-11df-85fd-d0968b3d1792	i2cat	0
df9fda50-3026-11df-85fd-d0968b3d1792	i2cat	0
dfa35cc0-3026-11df-85fd-d0968b3d1792	i2cat	0
18f484e0-3027-11df-85fd-d0968b3d1792	i2cat	0
18f9b500-3027-11df-85fd-d0968b3d1792	i2cat	0
19006bc0-3027-11df-85fd-d0968b3d1792	i2cat	0
1904b180-3027-11df-85fd-d0968b3d1792	i2cat	0
19099380-3027-11df-85fd-d0968b3d1792	i2cat	0
190eeab0-3027-11df-85fd-d0968b3d1792	i2cat	0
19133070-3027-11df-85fd-d0968b3d1792	i2cat	0
1916b2e0-3027-11df-85fd-d0968b3d1792	i2cat	0
191a3550-3027-11df-85fd-d0968b3d1792	i2cat	0
191e2cf0-3027-11df-85fd-d0968b3d1792	i2cat	0
19235d10-3027-11df-85fd-d0968b3d1792	i2cat	0
19277bc0-3027-11df-85fd-d0968b3d1792	i2cat	0
192b7360-3027-11df-85fd-d0968b3d1792	i2cat	0
192ef5d0-3027-11df-85fd-d0968b3d1792	i2cat	0
19331480-3027-11df-85fd-d0968b3d1792	i2cat	0
1938e0e0-3027-11df-85fd-d0968b3d1792	i2cat	0
193efb60-3027-11df-85fd-d0968b3d1792	i2cat	0
1942f300-3027-11df-85fd-d0968b3d1792	i2cat	0
\.


--
-- Data for Name: policy; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY policy (policy_key, operation, resource_key) FROM stdin;
9516d930-3025-11df-bd6b-a6bcc20293a6	create:userManagement	
9524bbe0-3025-11df-bd6b-a6bcc20293a6	create:router	
9527c920-3025-11df-bd6b-a6bcc20293a6	create:ipnetwork	
6b3489e0-3026-11df-85fd-d0968b3d1792	modify:userManagement	696c4d00-3026-11df-85fd-d0968b3d1792
6b3a0820-3026-11df-85fd-d0968b3d1792	destroy:userManagement	696c4d00-3026-11df-85fd-d0968b3d1792
6b4293a0-3026-11df-85fd-d0968b3d1792	find:userManagement	696c4d00-3026-11df-85fd-d0968b3d1792
6b4aa9f0-3026-11df-85fd-d0968b3d1792	getUsers:userManagement	696c4d00-3026-11df-85fd-d0968b3d1792
6b5187c0-3026-11df-85fd-d0968b3d1792	getMultipleResourceProperties:userManagement	696c4d00-3026-11df-85fd-d0968b3d1792
6b583e80-3026-11df-85fd-d0968b3d1792	setPolicies:policies	696c4d00-3026-11df-85fd-d0968b3d1792
93174330-3026-11df-85fd-d0968b3d1792	modify:userManagement	91c39a10-3026-11df-85fd-d0968b3d1792
931fceb0-3026-11df-85fd-d0968b3d1792	destroy:userManagement	91c39a10-3026-11df-85fd-d0968b3d1792
9326ac80-3026-11df-85fd-d0968b3d1792	find:userManagement	91c39a10-3026-11df-85fd-d0968b3d1792
932c03b0-3026-11df-85fd-d0968b3d1792	getUsers:userManagement	91c39a10-3026-11df-85fd-d0968b3d1792
93324540-3026-11df-85fd-d0968b3d1792	getMultipleResourceProperties:userManagement	91c39a10-3026-11df-85fd-d0968b3d1792
933811a0-3026-11df-85fd-d0968b3d1792	setPolicies:policies	91c39a10-3026-11df-85fd-d0968b3d1792
b49a8440-3026-11df-85fd-d0968b3d1792	find:router	b0595820-3026-11df-85fd-d0968b3d1792
b49fdb70-3026-11df-85fd-d0968b3d1792	createNewRouterInstance:router	b0595820-3026-11df-85fd-d0968b3d1792
b4a75580-3026-11df-85fd-d0968b3d1792	setPollingPeriod:router	b0595820-3026-11df-85fd-d0968b3d1792
b4ac3780-3026-11df-85fd-d0968b3d1792	invoke:router	b0595820-3026-11df-85fd-d0968b3d1792
b4b11980-3026-11df-85fd-d0968b3d1792	invoke:router	b0595820-3026-11df-85fd-d0968b3d1792
b4b697c0-3026-11df-85fd-d0968b3d1792	invoke:router	b0595820-3026-11df-85fd-d0968b3d1792
b4bc1600-3026-11df-85fd-d0968b3d1792	invoke:router	b0595820-3026-11df-85fd-d0968b3d1792
b4c2ccc0-3026-11df-85fd-d0968b3d1792	invoke:router	b0595820-3026-11df-85fd-d0968b3d1792
b4c8c030-3026-11df-85fd-d0968b3d1792	invoke:router	b0595820-3026-11df-85fd-d0968b3d1792
b4cdc940-3026-11df-85fd-d0968b3d1792	destroy:router	b0595820-3026-11df-85fd-d0968b3d1792
b4d2d250-3026-11df-85fd-d0968b3d1792	getMultipleResourceProperties:router	b0595820-3026-11df-85fd-d0968b3d1792
b4d67bd0-3026-11df-85fd-d0968b3d1792	modify:router	b0595820-3026-11df-85fd-d0968b3d1792
b4dae8a0-3026-11df-85fd-d0968b3d1792	getRouters:router	b0595820-3026-11df-85fd-d0968b3d1792
b4deb930-3026-11df-85fd-d0968b3d1792	setPolicies:policies	b0595820-3026-11df-85fd-d0968b3d1792
b7e47f20-3026-11df-85fd-d0968b3d1792	find:router	b4f9bb40-3026-11df-85fd-d0968b3d1792
b7eac0b0-3026-11df-85fd-d0968b3d1792	createNewRouterInstance:router	b4f9bb40-3026-11df-85fd-d0968b3d1792
b7f0b420-3026-11df-85fd-d0968b3d1792	setPollingPeriod:router	b4f9bb40-3026-11df-85fd-d0968b3d1792
b7f59620-3026-11df-85fd-d0968b3d1792	invoke:router	b4f9bb40-3026-11df-85fd-d0968b3d1792
b80a0880-3026-11df-85fd-d0968b3d1792	invoke:router	b4f9bb40-3026-11df-85fd-d0968b3d1792
b8115b80-3026-11df-85fd-d0968b3d1792	invoke:router	b4f9bb40-3026-11df-85fd-d0968b3d1792
b8157a30-3026-11df-85fd-d0968b3d1792	invoke:router	b4f9bb40-3026-11df-85fd-d0968b3d1792
b81b6da0-3026-11df-85fd-d0968b3d1792	invoke:router	b4f9bb40-3026-11df-85fd-d0968b3d1792
b821af30-3026-11df-85fd-d0968b3d1792	invoke:router	b4f9bb40-3026-11df-85fd-d0968b3d1792
b82531a0-3026-11df-85fd-d0968b3d1792	destroy:router	b4f9bb40-3026-11df-85fd-d0968b3d1792
b82ad6f0-3026-11df-85fd-d0968b3d1792	getMultipleResourceProperties:router	b4f9bb40-3026-11df-85fd-d0968b3d1792
b82f1cb0-3026-11df-85fd-d0968b3d1792	modify:router	b4f9bb40-3026-11df-85fd-d0968b3d1792
b832ed40-3026-11df-85fd-d0968b3d1792	getRouters:router	b4f9bb40-3026-11df-85fd-d0968b3d1792
b83696c0-3026-11df-85fd-d0968b3d1792	setPolicies:policies	b4f9bb40-3026-11df-85fd-d0968b3d1792
bb5c19b0-3026-11df-85fd-d0968b3d1792	find:router	b85c2020-3026-11df-85fd-d0968b3d1792
bb608680-3026-11df-85fd-d0968b3d1792	createNewRouterInstance:router	b85c2020-3026-11df-85fd-d0968b3d1792
bb662bd0-3026-11df-85fd-d0968b3d1792	setPollingPeriod:router	b85c2020-3026-11df-85fd-d0968b3d1792
bb6a7190-3026-11df-85fd-d0968b3d1792	invoke:router	b85c2020-3026-11df-85fd-d0968b3d1792
bb6f7aa0-3026-11df-85fd-d0968b3d1792	invoke:router	b85c2020-3026-11df-85fd-d0968b3d1792
bb75bc30-3026-11df-85fd-d0968b3d1792	invoke:router	b85c2020-3026-11df-85fd-d0968b3d1792
bb79b3d0-3026-11df-85fd-d0968b3d1792	invoke:router	b85c2020-3026-11df-85fd-d0968b3d1792
bb7ebce0-3026-11df-85fd-d0968b3d1792	invoke:router	b85c2020-3026-11df-85fd-d0968b3d1792
bb8573a0-3026-11df-85fd-d0968b3d1792	invoke:router	b85c2020-3026-11df-85fd-d0968b3d1792
bb8a0780-3026-11df-85fd-d0968b3d1792	destroy:router	b85c2020-3026-11df-85fd-d0968b3d1792
bb8d89f0-3026-11df-85fd-d0968b3d1792	getMultipleResourceProperties:router	b85c2020-3026-11df-85fd-d0968b3d1792
bb913370-3026-11df-85fd-d0968b3d1792	modify:router	b85c2020-3026-11df-85fd-d0968b3d1792
bb96b1b0-3026-11df-85fd-d0968b3d1792	getRouters:router	b85c2020-3026-11df-85fd-d0968b3d1792
bb9a8240-3026-11df-85fd-d0968b3d1792	setPolicies:policies	b85c2020-3026-11df-85fd-d0968b3d1792
bea689c0-3026-11df-85fd-d0968b3d1792	find:router	bba6b740-3026-11df-85fd-d0968b3d1792
bead1970-3026-11df-85fd-d0968b3d1792	createNewRouterInstance:router	bba6b740-3026-11df-85fd-d0968b3d1792
beb13820-3026-11df-85fd-d0968b3d1792	setPollingPeriod:router	bba6b740-3026-11df-85fd-d0968b3d1792
beb556d0-3026-11df-85fd-d0968b3d1792	invoke:router	bba6b740-3026-11df-85fd-d0968b3d1792
beba5fe0-3026-11df-85fd-d0968b3d1792	invoke:router	bba6b740-3026-11df-85fd-d0968b3d1792
bebf41e0-3026-11df-85fd-d0968b3d1792	invoke:router	bba6b740-3026-11df-85fd-d0968b3d1792
bec33980-3026-11df-85fd-d0968b3d1792	invoke:router	bba6b740-3026-11df-85fd-d0968b3d1792
bec92cf0-3026-11df-85fd-d0968b3d1792	invoke:router	bba6b740-3026-11df-85fd-d0968b3d1792
becd4ba0-3026-11df-85fd-d0968b3d1792	invoke:router	bba6b740-3026-11df-85fd-d0968b3d1792
bed11c30-3026-11df-85fd-d0968b3d1792	destroy:router	bba6b740-3026-11df-85fd-d0968b3d1792
bed53ae0-3026-11df-85fd-d0968b3d1792	getMultipleResourceProperties:router	bba6b740-3026-11df-85fd-d0968b3d1792
beda43f0-3026-11df-85fd-d0968b3d1792	modify:router	bba6b740-3026-11df-85fd-d0968b3d1792
bedded70-3026-11df-85fd-d0968b3d1792	getRouters:router	bba6b740-3026-11df-85fd-d0968b3d1792
bee16fe0-3026-11df-85fd-d0968b3d1792	setPolicies:policies	bba6b740-3026-11df-85fd-d0968b3d1792
c1ee13a0-3026-11df-85fd-d0968b3d1792	find:router	bef2fc10-3026-11df-85fd-d0968b3d1792
c1f3e000-3026-11df-85fd-d0968b3d1792	createNewRouterInstance:router	bef2fc10-3026-11df-85fd-d0968b3d1792
c1f9ac60-3026-11df-85fd-d0968b3d1792	setPollingPeriod:router	bef2fc10-3026-11df-85fd-d0968b3d1792
c1fe1930-3026-11df-85fd-d0968b3d1792	invoke:router	bef2fc10-3026-11df-85fd-d0968b3d1792
c2032240-3026-11df-85fd-d0968b3d1792	invoke:router	bef2fc10-3026-11df-85fd-d0968b3d1792
c208c790-3026-11df-85fd-d0968b3d1792	invoke:router	bef2fc10-3026-11df-85fd-d0968b3d1792
c20ebb00-3026-11df-85fd-d0968b3d1792	invoke:router	bef2fc10-3026-11df-85fd-d0968b3d1792
c2143940-3026-11df-85fd-d0968b3d1792	invoke:router	bef2fc10-3026-11df-85fd-d0968b3d1792
c21a05a0-3026-11df-85fd-d0968b3d1792	invoke:router	bef2fc10-3026-11df-85fd-d0968b3d1792
c21d6100-3026-11df-85fd-d0968b3d1792	destroy:router	bef2fc10-3026-11df-85fd-d0968b3d1792
c220bc60-3026-11df-85fd-d0968b3d1792	getMultipleResourceProperties:router	bef2fc10-3026-11df-85fd-d0968b3d1792
c225c570-3026-11df-85fd-d0968b3d1792	modify:router	bef2fc10-3026-11df-85fd-d0968b3d1792
c2296ef0-3026-11df-85fd-d0968b3d1792	getRouters:router	bef2fc10-3026-11df-85fd-d0968b3d1792
c22cf160-3026-11df-85fd-d0968b3d1792	setPolicies:policies	bef2fc10-3026-11df-85fd-d0968b3d1792
c568bb70-3026-11df-85fd-d0968b3d1792	find:router	c259cdc0-3026-11df-85fd-d0968b3d1792
c56d9d70-3026-11df-85fd-d0968b3d1792	createNewRouterInstance:router	c259cdc0-3026-11df-85fd-d0968b3d1792
c5719510-3026-11df-85fd-d0968b3d1792	setPollingPeriod:router	c259cdc0-3026-11df-85fd-d0968b3d1792
c576ec40-3026-11df-85fd-d0968b3d1792	invoke:router	c259cdc0-3026-11df-85fd-d0968b3d1792
c57ae3e0-3026-11df-85fd-d0968b3d1792	invoke:router	c259cdc0-3026-11df-85fd-d0968b3d1792
c57f29a0-3026-11df-85fd-d0968b3d1792	invoke:router	c259cdc0-3026-11df-85fd-d0968b3d1792
c582fa30-3026-11df-85fd-d0968b3d1792	invoke:router	c259cdc0-3026-11df-85fd-d0968b3d1792
c588c690-3026-11df-85fd-d0968b3d1792	invoke:router	c259cdc0-3026-11df-85fd-d0968b3d1792
c58d8180-3026-11df-85fd-d0968b3d1792	invoke:router	c259cdc0-3026-11df-85fd-d0968b3d1792
c590b5d0-3026-11df-85fd-d0968b3d1792	destroy:router	c259cdc0-3026-11df-85fd-d0968b3d1792
c59570c0-3026-11df-85fd-d0968b3d1792	getMultipleResourceProperties:router	c259cdc0-3026-11df-85fd-d0968b3d1792
c598cc20-3026-11df-85fd-d0968b3d1792	modify:router	c259cdc0-3026-11df-85fd-d0968b3d1792
c59d38f0-3026-11df-85fd-d0968b3d1792	getRouters:router	c259cdc0-3026-11df-85fd-d0968b3d1792
c5a1f3e0-3026-11df-85fd-d0968b3d1792	setPolicies:policies	c259cdc0-3026-11df-85fd-d0968b3d1792
c88f76e0-3026-11df-85fd-d0968b3d1792	find:router	c5b22080-3026-11df-85fd-d0968b3d1792
c89431d0-3026-11df-85fd-d0968b3d1792	createNewRouterInstance:router	c5b22080-3026-11df-85fd-d0968b3d1792
c8976620-3026-11df-85fd-d0968b3d1792	setPollingPeriod:router	c5b22080-3026-11df-85fd-d0968b3d1792
c89d5990-3026-11df-85fd-d0968b3d1792	invoke:router	c5b22080-3026-11df-85fd-d0968b3d1792
c8a10310-3026-11df-85fd-d0968b3d1792	invoke:router	c5b22080-3026-11df-85fd-d0968b3d1792
c8a4ac90-3026-11df-85fd-d0968b3d1792	invoke:router	c5b22080-3026-11df-85fd-d0968b3d1792
c8a94070-3026-11df-85fd-d0968b3d1792	invoke:router	c5b22080-3026-11df-85fd-d0968b3d1792
c8ad3810-3026-11df-85fd-d0968b3d1792	invoke:router	c5b22080-3026-11df-85fd-d0968b3d1792
c8b108a0-3026-11df-85fd-d0968b3d1792	invoke:router	c5b22080-3026-11df-85fd-d0968b3d1792
c8b43cf0-3026-11df-85fd-d0968b3d1792	destroy:router	c5b22080-3026-11df-85fd-d0968b3d1792
c8b8a9c0-3026-11df-85fd-d0968b3d1792	getMultipleResourceProperties:router	c5b22080-3026-11df-85fd-d0968b3d1792
c8bc0520-3026-11df-85fd-d0968b3d1792	modify:router	c5b22080-3026-11df-85fd-d0968b3d1792
c8bffcc0-3026-11df-85fd-d0968b3d1792	getRouters:router	c5b22080-3026-11df-85fd-d0968b3d1792
c8c37f30-3026-11df-85fd-d0968b3d1792	setPolicies:policies	c5b22080-3026-11df-85fd-d0968b3d1792
cb9c8fd0-3026-11df-85fd-d0968b3d1792	find:router	c8ce54a0-3026-11df-85fd-d0968b3d1792
cba20e10-3026-11df-85fd-d0968b3d1792	createNewRouterInstance:router	c8ce54a0-3026-11df-85fd-d0968b3d1792
cba59080-3026-11df-85fd-d0968b3d1792	setPollingPeriod:router	c8ce54a0-3026-11df-85fd-d0968b3d1792
cbaa7280-3026-11df-85fd-d0968b3d1792	invoke:router	c8ce54a0-3026-11df-85fd-d0968b3d1792
cbae9130-3026-11df-85fd-d0968b3d1792	invoke:router	c8ce54a0-3026-11df-85fd-d0968b3d1792
cbb2fe00-3026-11df-85fd-d0968b3d1792	invoke:router	c8ce54a0-3026-11df-85fd-d0968b3d1792
cbb6f5a0-3026-11df-85fd-d0968b3d1792	invoke:router	c8ce54a0-3026-11df-85fd-d0968b3d1792
cbbd3730-3026-11df-85fd-d0968b3d1792	invoke:router	c8ce54a0-3026-11df-85fd-d0968b3d1792
cbc28e60-3026-11df-85fd-d0968b3d1792	invoke:router	c8ce54a0-3026-11df-85fd-d0968b3d1792
cbc637e0-3026-11df-85fd-d0968b3d1792	destroy:router	c8ce54a0-3026-11df-85fd-d0968b3d1792
cbcb6800-3026-11df-85fd-d0968b3d1792	getMultipleResourceProperties:router	c8ce54a0-3026-11df-85fd-d0968b3d1792
cbd37e50-3026-11df-85fd-d0968b3d1792	modify:router	c8ce54a0-3026-11df-85fd-d0968b3d1792
cbd83940-3026-11df-85fd-d0968b3d1792	getRouters:router	c8ce54a0-3026-11df-85fd-d0968b3d1792
cbde53c0-3026-11df-85fd-d0968b3d1792	setPolicies:policies	c8ce54a0-3026-11df-85fd-d0968b3d1792
ced6ac30-3026-11df-85fd-d0968b3d1792	find:router	cbee3240-3026-11df-85fd-d0968b3d1792
cedd3be0-3026-11df-85fd-d0968b3d1792	createNewRouterInstance:router	cbee3240-3026-11df-85fd-d0968b3d1792
cee37d70-3026-11df-85fd-d0968b3d1792	setPollingPeriod:router	cbee3240-3026-11df-85fd-d0968b3d1792
ceea3430-3026-11df-85fd-d0968b3d1792	invoke:router	cbee3240-3026-11df-85fd-d0968b3d1792
ceee2bd0-3026-11df-85fd-d0968b3d1792	invoke:router	cbee3240-3026-11df-85fd-d0968b3d1792
cef27190-3026-11df-85fd-d0968b3d1792	invoke:router	cbee3240-3026-11df-85fd-d0968b3d1792
cef83df0-3026-11df-85fd-d0968b3d1792	invoke:router	cbee3240-3026-11df-85fd-d0968b3d1792
cefd4700-3026-11df-85fd-d0968b3d1792	invoke:router	cbee3240-3026-11df-85fd-d0968b3d1792
cf0165b0-3026-11df-85fd-d0968b3d1792	invoke:router	cbee3240-3026-11df-85fd-d0968b3d1792
cf04e820-3026-11df-85fd-d0968b3d1792	destroy:router	cbee3240-3026-11df-85fd-d0968b3d1792
cf09ca20-3026-11df-85fd-d0968b3d1792	getMultipleResourceProperties:router	cbee3240-3026-11df-85fd-d0968b3d1792
cf0e0fe0-3026-11df-85fd-d0968b3d1792	modify:router	cbee3240-3026-11df-85fd-d0968b3d1792
cf12a3c0-3026-11df-85fd-d0968b3d1792	getRouters:router	cbee3240-3026-11df-85fd-d0968b3d1792
cf15ff20-3026-11df-85fd-d0968b3d1792	setPolicies:policies	cbee3240-3026-11df-85fd-d0968b3d1792
d2100540-3026-11df-85fd-d0968b3d1792	find:router	cf293900-3026-11df-85fd-d0968b3d1792
d2149920-3026-11df-85fd-d0968b3d1792	createNewRouterInstance:router	cf293900-3026-11df-85fd-d0968b3d1792
d21905f0-3026-11df-85fd-d0968b3d1792	setPollingPeriod:router	cf293900-3026-11df-85fd-d0968b3d1792
d2214350-3026-11df-85fd-d0968b3d1792	invoke:router	cf293900-3026-11df-85fd-d0968b3d1792
d225b020-3026-11df-85fd-d0968b3d1792	invoke:router	cf293900-3026-11df-85fd-d0968b3d1792
d22a4400-3026-11df-85fd-d0968b3d1792	invoke:router	cf293900-3026-11df-85fd-d0968b3d1792
d22e62b0-3026-11df-85fd-d0968b3d1792	invoke:router	cf293900-3026-11df-85fd-d0968b3d1792
d233b9e0-3026-11df-85fd-d0968b3d1792	invoke:router	cf293900-3026-11df-85fd-d0968b3d1792
d237b180-3026-11df-85fd-d0968b3d1792	invoke:router	cf293900-3026-11df-85fd-d0968b3d1792
d23b33f0-3026-11df-85fd-d0968b3d1792	destroy:router	cf293900-3026-11df-85fd-d0968b3d1792
d23edd70-3026-11df-85fd-d0968b3d1792	getMultipleResourceProperties:router	cf293900-3026-11df-85fd-d0968b3d1792
d26ea000-3026-11df-85fd-d0968b3d1792	modify:router	cf293900-3026-11df-85fd-d0968b3d1792
d2730cd0-3026-11df-85fd-d0968b3d1792	getRouters:router	cf293900-3026-11df-85fd-d0968b3d1792
d276b650-3026-11df-85fd-d0968b3d1792	setPolicies:policies	cf293900-3026-11df-85fd-d0968b3d1792
d5724310-3026-11df-85fd-d0968b3d1792	find:router	d285d180-3026-11df-85fd-d0968b3d1792
d5777330-3026-11df-85fd-d0968b3d1792	createNewRouterInstance:router	d285d180-3026-11df-85fd-d0968b3d1792
d57c0710-3026-11df-85fd-d0968b3d1792	setPollingPeriod:router	d285d180-3026-11df-85fd-d0968b3d1792
d581fa80-3026-11df-85fd-d0968b3d1792	invoke:router	d285d180-3026-11df-85fd-d0968b3d1792
d5883c10-3026-11df-85fd-d0968b3d1792	invoke:router	d285d180-3026-11df-85fd-d0968b3d1792
d58ca8e0-3026-11df-85fd-d0968b3d1792	invoke:router	d285d180-3026-11df-85fd-d0968b3d1792
d590a080-3026-11df-85fd-d0968b3d1792	invoke:router	d285d180-3026-11df-85fd-d0968b3d1792
d5955b70-3026-11df-85fd-d0968b3d1792	invoke:router	d285d180-3026-11df-85fd-d0968b3d1792
d59b27d0-3026-11df-85fd-d0968b3d1792	invoke:router	d285d180-3026-11df-85fd-d0968b3d1792
d59f6d90-3026-11df-85fd-d0968b3d1792	destroy:router	d285d180-3026-11df-85fd-d0968b3d1792
d5a512e0-3026-11df-85fd-d0968b3d1792	getMultipleResourceProperties:router	d285d180-3026-11df-85fd-d0968b3d1792
d5a8e370-3026-11df-85fd-d0968b3d1792	modify:router	d285d180-3026-11df-85fd-d0968b3d1792
d5ae61b0-3026-11df-85fd-d0968b3d1792	getRouters:router	d285d180-3026-11df-85fd-d0968b3d1792
d5b31ca0-3026-11df-85fd-d0968b3d1792	setPolicies:policies	d285d180-3026-11df-85fd-d0968b3d1792
d8c47b50-3026-11df-85fd-d0968b3d1792	find:router	d5d34ed0-3026-11df-85fd-d0968b3d1792
d8cbf560-3026-11df-85fd-d0968b3d1792	createNewRouterInstance:router	d5d34ed0-3026-11df-85fd-d0968b3d1792
d8cfc5f0-3026-11df-85fd-d0968b3d1792	setPollingPeriod:router	d5d34ed0-3026-11df-85fd-d0968b3d1792
d8d4cf00-3026-11df-85fd-d0968b3d1792	invoke:router	d5d34ed0-3026-11df-85fd-d0968b3d1792
d8d9b100-3026-11df-85fd-d0968b3d1792	invoke:router	d5d34ed0-3026-11df-85fd-d0968b3d1792
d8dfa470-3026-11df-85fd-d0968b3d1792	invoke:router	d5d34ed0-3026-11df-85fd-d0968b3d1792
d8e39c10-3026-11df-85fd-d0968b3d1792	invoke:router	d5d34ed0-3026-11df-85fd-d0968b3d1792
d8e8cc30-3026-11df-85fd-d0968b3d1792	invoke:router	d5d34ed0-3026-11df-85fd-d0968b3d1792
d8edfc50-3026-11df-85fd-d0968b3d1792	invoke:router	d5d34ed0-3026-11df-85fd-d0968b3d1792
d8f2de50-3026-11df-85fd-d0968b3d1792	destroy:router	d5d34ed0-3026-11df-85fd-d0968b3d1792
d8f639b0-3026-11df-85fd-d0968b3d1792	getMultipleResourceProperties:router	d5d34ed0-3026-11df-85fd-d0968b3d1792
d8f9e330-3026-11df-85fd-d0968b3d1792	modify:router	d5d34ed0-3026-11df-85fd-d0968b3d1792
d8fd8cb0-3026-11df-85fd-d0968b3d1792	getRouters:router	d5d34ed0-3026-11df-85fd-d0968b3d1792
d90295c0-3026-11df-85fd-d0968b3d1792	setPolicies:policies	d5d34ed0-3026-11df-85fd-d0968b3d1792
dc292a20-3026-11df-85fd-d0968b3d1792	find:router	d916e110-3026-11df-85fd-d0968b3d1792
dc2e5a40-3026-11df-85fd-d0968b3d1792	createNewRouterInstance:router	d916e110-3026-11df-85fd-d0968b3d1792
dc349bd0-3026-11df-85fd-d0968b3d1792	setPollingPeriod:router	d916e110-3026-11df-85fd-d0968b3d1792
dc3add60-3026-11df-85fd-d0968b3d1792	invoke:router	d916e110-3026-11df-85fd-d0968b3d1792
dc3f9850-3026-11df-85fd-d0968b3d1792	invoke:router	d916e110-3026-11df-85fd-d0968b3d1792
dc43de10-3026-11df-85fd-d0968b3d1792	invoke:router	d916e110-3026-11df-85fd-d0968b3d1792
dc48c010-3026-11df-85fd-d0968b3d1792	invoke:router	d916e110-3026-11df-85fd-d0968b3d1792
dc4eb380-3026-11df-85fd-d0968b3d1792	invoke:router	d916e110-3026-11df-85fd-d0968b3d1792
dc534760-3026-11df-85fd-d0968b3d1792	invoke:router	d916e110-3026-11df-85fd-d0968b3d1792
dc56c9d0-3026-11df-85fd-d0968b3d1792	destroy:router	d916e110-3026-11df-85fd-d0968b3d1792
dc5a4c40-3026-11df-85fd-d0968b3d1792	getMultipleResourceProperties:router	d916e110-3026-11df-85fd-d0968b3d1792
dc5f0730-3026-11df-85fd-d0968b3d1792	modify:router	d916e110-3026-11df-85fd-d0968b3d1792
dc62fed0-3026-11df-85fd-d0968b3d1792	getRouters:router	d916e110-3026-11df-85fd-d0968b3d1792
dc671d80-3026-11df-85fd-d0968b3d1792	setPolicies:policies	d916e110-3026-11df-85fd-d0968b3d1792
df671710-3026-11df-85fd-d0968b3d1792	find:router	dc8af930-3026-11df-85fd-d0968b3d1792
df6bd200-3026-11df-85fd-d0968b3d1792	createNewRouterInstance:router	dc8af930-3026-11df-85fd-d0968b3d1792
df710220-3026-11df-85fd-d0968b3d1792	setPollingPeriod:router	dc8af930-3026-11df-85fd-d0968b3d1792
df76f590-3026-11df-85fd-d0968b3d1792	invoke:router	dc8af930-3026-11df-85fd-d0968b3d1792
df7bfea0-3026-11df-85fd-d0968b3d1792	invoke:router	dc8af930-3026-11df-85fd-d0968b3d1792
df806b70-3026-11df-85fd-d0968b3d1792	invoke:router	dc8af930-3026-11df-85fd-d0968b3d1792
df84b130-3026-11df-85fd-d0968b3d1792	invoke:router	dc8af930-3026-11df-85fd-d0968b3d1792
df894510-3026-11df-85fd-d0968b3d1792	invoke:router	dc8af930-3026-11df-85fd-d0968b3d1792
df8f5f90-3026-11df-85fd-d0968b3d1792	invoke:router	dc8af930-3026-11df-85fd-d0968b3d1792
df930910-3026-11df-85fd-d0968b3d1792	destroy:router	dc8af930-3026-11df-85fd-d0968b3d1792
df96b290-3026-11df-85fd-d0968b3d1792	getMultipleResourceProperties:router	dc8af930-3026-11df-85fd-d0968b3d1792
df9a3500-3026-11df-85fd-d0968b3d1792	modify:router	dc8af930-3026-11df-85fd-d0968b3d1792
df9fda50-3026-11df-85fd-d0968b3d1792	getRouters:router	dc8af930-3026-11df-85fd-d0968b3d1792
dfa35cc0-3026-11df-85fd-d0968b3d1792	setPolicies:policies	dc8af930-3026-11df-85fd-d0968b3d1792
18f484e0-3027-11df-85fd-d0968b3d1792	modify:ipnetwork	186b7ec0-3027-11df-85fd-d0968b3d1792
18f9b500-3027-11df-85fd-d0968b3d1792	getIPNetworks:ipnetwork	186b7ec0-3027-11df-85fd-d0968b3d1792
19006bc0-3027-11df-85fd-d0968b3d1792	find:ipnetwork	186b7ec0-3027-11df-85fd-d0968b3d1792
1904b180-3027-11df-85fd-d0968b3d1792	invoke:ipnetwork	186b7ec0-3027-11df-85fd-d0968b3d1792
19099380-3027-11df-85fd-d0968b3d1792	invoke:ipnetwork	186b7ec0-3027-11df-85fd-d0968b3d1792
190eeab0-3027-11df-85fd-d0968b3d1792	invoke:ipnetwork	186b7ec0-3027-11df-85fd-d0968b3d1792
19133070-3027-11df-85fd-d0968b3d1792	destroy:ipnetwork	186b7ec0-3027-11df-85fd-d0968b3d1792
1916b2e0-3027-11df-85fd-d0968b3d1792	addrouters:ipnetwork	186b7ec0-3027-11df-85fd-d0968b3d1792
191a3550-3027-11df-85fd-d0968b3d1792	deleteRouters:ipnetwork	186b7ec0-3027-11df-85fd-d0968b3d1792
191e2cf0-3027-11df-85fd-d0968b3d1792	addDirectedLink:ipnetwork	186b7ec0-3027-11df-85fd-d0968b3d1792
19235d10-3027-11df-85fd-d0968b3d1792	deleteDirectedLinks:ipnetwork	186b7ec0-3027-11df-85fd-d0968b3d1792
19277bc0-3027-11df-85fd-d0968b3d1792	queueAction:ipnetwork	186b7ec0-3027-11df-85fd-d0968b3d1792
192b7360-3027-11df-85fd-d0968b3d1792	getActionQueue:ipnetwork	186b7ec0-3027-11df-85fd-d0968b3d1792
192ef5d0-3027-11df-85fd-d0968b3d1792	commitQueue:ipnetwork	186b7ec0-3027-11df-85fd-d0968b3d1792
19331480-3027-11df-85fd-d0968b3d1792	deleteActionsFromQueue:ipnetwork	186b7ec0-3027-11df-85fd-d0968b3d1792
1938e0e0-3027-11df-85fd-d0968b3d1792	emptyQueue:ipnetwork	186b7ec0-3027-11df-85fd-d0968b3d1792
193efb60-3027-11df-85fd-d0968b3d1792	setPolicies:policies	186b7ec0-3027-11df-85fd-d0968b3d1792
1942f300-3027-11df-85fd-d0968b3d1792	setQueue:policies	186b7ec0-3027-11df-85fd-d0968b3d1792
\.


--
-- Data for Name: router_instance; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY router_instance (router_resource_id, router_name, model, parent, polling_period, "location", accessconfiguration) FROM stdin;
b4f9bb40-3026-11df-85fd-d0968b3d1792	R1	ROUTER_JUNIPERM10	b0595820-3026-11df-85fd-d0968b3d1792	0	1	1
b85c2020-3026-11df-85fd-d0968b3d1792	R2	ROUTER_JUNIPERM10	b0595820-3026-11df-85fd-d0968b3d1792	0	2	2
bba6b740-3026-11df-85fd-d0968b3d1792	R3	ROUTER_JUNIPERM10	b0595820-3026-11df-85fd-d0968b3d1792	0	3	3
bef2fc10-3026-11df-85fd-d0968b3d1792	R4	ROUTER_JUNIPERM10	b0595820-3026-11df-85fd-d0968b3d1792	0	4	4
c259cdc0-3026-11df-85fd-d0968b3d1792	R5	ROUTER_JUNIPERM10	b0595820-3026-11df-85fd-d0968b3d1792	0	5	5
c5b22080-3026-11df-85fd-d0968b3d1792	R6	ROUTER_JUNIPERM10	b0595820-3026-11df-85fd-d0968b3d1792	0	6	6
c8ce54a0-3026-11df-85fd-d0968b3d1792	R7	ROUTER_JUNIPERM10	b0595820-3026-11df-85fd-d0968b3d1792	0	7	7
cbee3240-3026-11df-85fd-d0968b3d1792	R8	ROUTER_JUNIPERM10	b0595820-3026-11df-85fd-d0968b3d1792	0	8	8
cf293900-3026-11df-85fd-d0968b3d1792	RLIE	ROUTER_JUNIPERM10	b0595820-3026-11df-85fd-d0968b3d1792	0	9	9
d285d180-3026-11df-85fd-d0968b3d1792	logicalJuniper1	ROUTER_JUNIPERM10	b0595820-3026-11df-85fd-d0968b3d1792	0	10	10
d5d34ed0-3026-11df-85fd-d0968b3d1792	logicalRIP1	ROUTER_JUNIPERM10	b0595820-3026-11df-85fd-d0968b3d1792	0	11	11
d916e110-3026-11df-85fd-d0968b3d1792	routerTest	ROUTER_JUNIPERM10	b0595820-3026-11df-85fd-d0968b3d1792	0	12	12
dc8af930-3026-11df-85fd-d0968b3d1792	routerV2	ROUTER_JUNIPERM10	b0595820-3026-11df-85fd-d0968b3d1792	0	13	13
b0595820-3026-11df-85fd-d0968b3d1792	m10	ROUTER_JUNIPERM10	\N	0	14	14
\.


--
-- Data for Name: router_instance_access_configuration; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY router_instance_access_configuration (access_configuration_id, ip_access_address, port, transport_name, protocol_name) FROM stdin;
1	194.68.13.29	22	SSH	NETCONF
2	194.68.13.29	22	SSH	NETCONF
3	194.68.13.29	22	SSH	NETCONF
4	194.68.13.29	22	SSH	NETCONF
5	194.68.13.29	22	SSH	NETCONF
6	194.68.13.29	22	SSH	NETCONF
7	194.68.13.29	22	SSH	NETCONF
8	194.68.13.29	22	SSH	NETCONF
9	194.68.13.29	22	SSH	NETCONF
10	194.68.13.29	22	SSH	NETCONF
11	194.68.13.29	22	SSH	NETCONF
12	194.68.13.29	22	SSH	NETCONF
13	194.68.13.29	22	SSH	NETCONF
14	194.68.13.29	22	SSH	NETCONF
\.


--
-- Data for Name: router_instance_children; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY router_instance_children (router_instance_id, children_key, "position") FROM stdin;
b0595820-3026-11df-85fd-d0968b3d1792	b4f9bb40-3026-11df-85fd-d0968b3d1792	0
b0595820-3026-11df-85fd-d0968b3d1792	b85c2020-3026-11df-85fd-d0968b3d1792	1
b0595820-3026-11df-85fd-d0968b3d1792	bba6b740-3026-11df-85fd-d0968b3d1792	2
b0595820-3026-11df-85fd-d0968b3d1792	bef2fc10-3026-11df-85fd-d0968b3d1792	3
b0595820-3026-11df-85fd-d0968b3d1792	c259cdc0-3026-11df-85fd-d0968b3d1792	4
b0595820-3026-11df-85fd-d0968b3d1792	c5b22080-3026-11df-85fd-d0968b3d1792	5
b0595820-3026-11df-85fd-d0968b3d1792	c8ce54a0-3026-11df-85fd-d0968b3d1792	6
b0595820-3026-11df-85fd-d0968b3d1792	cbee3240-3026-11df-85fd-d0968b3d1792	7
b0595820-3026-11df-85fd-d0968b3d1792	cf293900-3026-11df-85fd-d0968b3d1792	8
b0595820-3026-11df-85fd-d0968b3d1792	d285d180-3026-11df-85fd-d0968b3d1792	9
b0595820-3026-11df-85fd-d0968b3d1792	d5d34ed0-3026-11df-85fd-d0968b3d1792	10
b0595820-3026-11df-85fd-d0968b3d1792	d916e110-3026-11df-85fd-d0968b3d1792	11
b0595820-3026-11df-85fd-d0968b3d1792	dc8af930-3026-11df-85fd-d0968b3d1792	12
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
14						0	0	
\.


--
-- Data for Name: router_instance_user_account; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY router_instance_user_account (user_account_id, user_id, psw, privileged_user, privileged_psw, router_configured, smtp_server, smtp_server_port, email_user, email_password, router_instance_id) FROM stdin;
1	i2CAT	Yct4KgYR4F3fdhRV			R1	\N	\N	\N	\N	b4f9bb40-3026-11df-85fd-d0968b3d1792
2	i2CAT	Yct4KgYR4F3fdhRV			R2	\N	\N	\N	\N	b85c2020-3026-11df-85fd-d0968b3d1792
3	i2CAT	Yct4KgYR4F3fdhRV			R3	\N	\N	\N	\N	bba6b740-3026-11df-85fd-d0968b3d1792
4	i2CAT	Yct4KgYR4F3fdhRV			R4	\N	\N	\N	\N	bef2fc10-3026-11df-85fd-d0968b3d1792
5	i2CAT	Yct4KgYR4F3fdhRV			R5	\N	\N	\N	\N	c259cdc0-3026-11df-85fd-d0968b3d1792
6	i2CAT	Yct4KgYR4F3fdhRV			R6	\N	\N	\N	\N	c5b22080-3026-11df-85fd-d0968b3d1792
7	i2CAT	Yct4KgYR4F3fdhRV			R7	\N	\N	\N	\N	c8ce54a0-3026-11df-85fd-d0968b3d1792
8	i2CAT	Yct4KgYR4F3fdhRV			R8	\N	\N	\N	\N	cbee3240-3026-11df-85fd-d0968b3d1792
9	i2CAT	Yct4KgYR4F3fdhRV			RLIE	\N	\N	\N	\N	cf293900-3026-11df-85fd-d0968b3d1792
10	i2CAT	Yct4KgYR4F3fdhRV			logicalJuniper1	\N	\N	\N	\N	d285d180-3026-11df-85fd-d0968b3d1792
11	i2CAT	Yct4KgYR4F3fdhRV			logicalRIP1	\N	\N	\N	\N	d5d34ed0-3026-11df-85fd-d0968b3d1792
12	i2CAT	Yct4KgYR4F3fdhRV			routerTest	\N	\N	\N	\N	d916e110-3026-11df-85fd-d0968b3d1792
13	i2CAT	Yct4KgYR4F3fdhRV			routerV2	\N	\N	\N	\N	dc8af930-3026-11df-85fd-d0968b3d1792
14	i2CAT	Yct4KgYR4F3fdhRV			\N	\N	\N	\N	\N	b0595820-3026-11df-85fd-d0968b3d1792
\.


--
-- Data for Name: user_instance; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY user_instance (resource_id, user_name, first_name, last_name, organization, address, telephone, email, "role") FROM stdin;
696c4d00-3026-11df-85fd-d0968b3d1792	i2cat	i2cat	i2cat	ITI				Administrator
91c39a10-3026-11df-85fd-d0968b3d1792	i2cat-user	i2cat-user	i2cat-user	ITI				User
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

