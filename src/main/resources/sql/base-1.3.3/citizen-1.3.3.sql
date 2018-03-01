--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

--
-- Name: address_out_ap_seq; Type: SEQUENCE; Schema: public; Owner: citizen_user
--

CREATE SEQUENCE address_out_ap_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE address_out_ap_seq OWNER TO citizen_user;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: age_group_ref; Type: TABLE; Schema: public; Owner: citizen_user; Tablespace: 
--

CREATE TABLE age_group_ref (
    age_group_cd character varying(255) NOT NULL,
    created_by character varying(50),
    created_on bigint,
    modified_by character varying(50),
    modified_on bigint,
    age_end integer,
    age_group_desc character varying(255),
    age_start integer,
    is_active character varying(255)
);


ALTER TABLE age_group_ref OWNER TO citizen_user;

--
-- Name: app_history_seq; Type: SEQUENCE; Schema: public; Owner: citizen_user
--

CREATE SEQUENCE app_history_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE app_history_seq OWNER TO citizen_user;

--
-- Name: app_removed_exe_id_seq; Type: SEQUENCE; Schema: public; Owner: citizen_user
--

CREATE SEQUENCE app_removed_exe_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE app_removed_exe_id_seq OWNER TO citizen_user;

--
-- Name: application; Type: TABLE; Schema: public; Owner: citizen_user; Tablespace: 
--

CREATE TABLE application (
    application_id bigint NOT NULL,
    created_by character varying(50),
    created_on bigint,
    modified_by character varying(50),
    modified_on bigint,
    applicant_dob character varying(255),
    application_number character varying(255),
    execution_id character varying(255),
    is_authenticated boolean,
    is_sync_with_master boolean DEFAULT false,
    iteration integer DEFAULT 1,
    process_id character varying(255),
    rta_office_code character varying(255),
    service_category character varying(20),
    service_code character varying(255),
    session_id bigint NOT NULL
);


ALTER TABLE application OWNER TO citizen_user;

--
-- Name: application_approval_history; Type: TABLE; Schema: public; Owner: citizen_user; Tablespace: 
--

CREATE TABLE application_approval_history (
    app_history_id bigint NOT NULL,
    created_by character varying(50),
    created_on bigint,
    modified_by character varying(50),
    modified_on bigint,
    comments character varying(255),
    iteration integer,
    rta_user_id bigint,
    rta_user_role character varying(255),
    status integer,
    application_id bigint NOT NULL
);


ALTER TABLE application_approval_history OWNER TO citizen_user;

--
-- Name: application_attachments; Type: TABLE; Schema: public; Owner: citizen_user; Tablespace: 
--

CREATE TABLE application_attachments (
    attachment_id bigint NOT NULL,
    created_by character varying(50),
    created_on bigint,
    modified_by character varying(50),
    modified_on bigint,
    attachment_from integer NOT NULL,
    title character varying(200) NOT NULL,
    filename character varying(200) NOT NULL,
    source_dr character varying(255) NOT NULL,
    status integer NOT NULL,
    user_type character varying(255),
    application_id bigint NOT NULL,
    document_id integer NOT NULL
);


ALTER TABLE application_attachments OWNER TO citizen_user;

--
-- Name: application_attachments_seq; Type: SEQUENCE; Schema: public; Owner: citizen_user
--

CREATE SEQUENCE application_attachments_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE application_attachments_seq OWNER TO citizen_user;

--
-- Name: application_bank_transaction_detail; Type: TABLE; Schema: public; Owner: citizen_user; Tablespace: 
--

CREATE TABLE application_bank_transaction_detail (
    transaction_id bigint NOT NULL,
    created_by character varying(50),
    created_on bigint,
    modified_by character varying(50),
    modified_on bigint,
    fee_amount double precision,
    bank_status_message character varying(255),
    bank_transac_no character varying(255) NOT NULL,
    compound_amount double precision,
    green_tax_amt double precision,
    pay_amount double precision,
    payment_time bigint,
    payment_type integer,
    permit_amount double precision,
    postal_charge double precision,
    sbi_ref_no character varying(255),
    service_charge double precision,
    service_code character varying(255),
    status integer,
    tax_amount double precision,
    application_id bigint,
    cess_fee double precision
);


ALTER TABLE application_bank_transaction_detail OWNER TO citizen_user;

--
-- Name: application_fee_detail; Type: TABLE; Schema: public; Owner: citizen_user; Tablespace: 
--

CREATE TABLE application_fee_detail (
    fee_dtl_id bigint NOT NULL,
    created_by character varying(50),
    created_on bigint,
    modified_by character varying(50),
    modified_on bigint,
    other_permit_fee double precision,
    application_fee double precision,
    application_service_charge double precision,
    fitness_fee double precision,
    fitness_service_charge double precision,
    license_test_fee double precision,
    penality_fee double precision,
    permit_fee double precision,
    permit_service_charge double precision,
    postal_charge double precision,
    smart_card_fee double precision,
    total_fee double precision,
    application_id bigint,
    late_fee double precision,
    hsrp_fee integer DEFAULT 0,
    special_number_fee integer DEFAULT 0
);


ALTER TABLE application_fee_detail OWNER TO citizen_user;

--
-- Name: application_form_data; Type: TABLE; Schema: public; Owner: citizen_user; Tablespace: 
--

CREATE TABLE application_form_data (
    data_id bigint NOT NULL,
    created_by character varying(50),
    created_on bigint,
    modified_by character varying(50),
    modified_on bigint,
    status integer,
    application_id bigint NOT NULL,
    form_data json,
    form_code character varying(255)
);


ALTER TABLE application_form_data OWNER TO citizen_user;

--
-- Name: application_seq; Type: SEQUENCE; Schema: public; Owner: citizen_user
--

CREATE SEQUENCE application_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE application_seq OWNER TO citizen_user;

--
-- Name: application_tax_detail; Type: TABLE; Schema: public; Owner: citizen_user; Tablespace: 
--

CREATE TABLE application_tax_detail (
    tax_dtl_id bigint NOT NULL,
    created_by character varying(50),
    created_on bigint,
    modified_by character varying(50),
    modified_on bigint,
    green_tax_amt integer DEFAULT 0,
    green_tax_valid_from bigint,
    tax_amount double precision,
    tax_percentage double precision,
    tax_type character varying(255),
    total_amt double precision,
    valid_upto bigint,
    application_id bigint,
    green_tax_valid_to bigint,
    penalty_amt integer DEFAULT 0,
    quarter_amt integer DEFAULT 0,
    service_fee integer DEFAULT 0,
    cess_fee integer DEFAULT 0,
    cess_fee_valid_upto bigint,
    compound_fee integer DEFAULT 0,
    penalty_amt_arrears integer DEFAULT 0,
    tax_amount_arrears integer DEFAULT 0
);


ALTER TABLE application_tax_detail OWNER TO citizen_user;

--
-- Name: citizen_address; Type: TABLE; Schema: public; Owner: citizen_user; Tablespace: 
--

CREATE TABLE citizen_address (
    address_id bigint NOT NULL,
    created_by character varying(50),
    created_on bigint,
    modified_by character varying(50),
    modified_on bigint,
    address_type integer DEFAULT 1,
    country_code character varying(255),
    district_code character varying(255),
    door_no character varying(255),
    is_same_aadhar boolean DEFAULT false,
    mandal_code integer,
    pincode character varying(255),
    state_code character varying(255),
    status boolean,
    street character varying(255),
    town character varying(255),
    with_effect_from bigint,
    application_id bigint NOT NULL
);


ALTER TABLE citizen_address OWNER TO citizen_user;

--
-- Name: citizen_address_outside_ap; Type: TABLE; Schema: public; Owner: citizen_user; Tablespace: 
--

CREATE TABLE citizen_address_outside_ap (
    address_id bigint NOT NULL,
    created_by character varying(50),
    created_on bigint,
    modified_by character varying(50),
    modified_on bigint,
    address_type integer DEFAULT 1,
    country_name character varying(255),
    district_name character varying(255),
    door_no character varying(255),
    mandal_name character varying(255),
    pincode character varying(6),
    state_name character varying(255),
    staus boolean,
    street_name character varying(255),
    town_name character varying(255),
    with_effect_from bigint,
    application_id bigint NOT NULL
);


ALTER TABLE citizen_address_outside_ap OWNER TO citizen_user;

--
-- Name: citizen_address_seq; Type: SEQUENCE; Schema: public; Owner: citizen_user
--

CREATE SEQUENCE citizen_address_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE citizen_address_seq OWNER TO citizen_user;

--
-- Name: citizen_info; Type: TABLE; Schema: public; Owner: citizen_user; Tablespace: 
--

CREATE TABLE citizen_info (
    citizen_info_id bigint NOT NULL,
    aadhar_number character varying(255),
    application_id bigint,
    father_name character varying(255),
    first_name character varying(255),
    full_name character varying(255),
    gender character varying(255),
    last_name character varying(255),
    middle_name character varying(255)
);


ALTER TABLE citizen_info OWNER TO citizen_user;

--
-- Name: citizen_info_seq; Type: SEQUENCE; Schema: public; Owner: citizen_user
--

CREATE SEQUENCE citizen_info_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE citizen_info_seq OWNER TO citizen_user;

--
-- Name: citizen_inv_seq; Type: SEQUENCE; Schema: public; Owner: citizen_user
--

CREATE SEQUENCE citizen_inv_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE citizen_inv_seq OWNER TO citizen_user;

--
-- Name: citizen_invoice; Type: TABLE; Schema: public; Owner: citizen_user; Tablespace: 
--

CREATE TABLE citizen_invoice (
    citizen_invc_id bigint NOT NULL,
    created_by character varying(50),
    created_on bigint,
    modified_by character varying(50),
    modified_on bigint,
    invoice_amt double precision,
    invoice_date bigint,
    invoice_no character varying(255),
    status integer,
    total_amount double precision,
    application_id bigint,
    fee_dtl_id bigint,
    tax_dtl_id bigint
);


ALTER TABLE citizen_invoice OWNER TO citizen_user;

--
-- Name: data_id_seq; Type: SEQUENCE; Schema: public; Owner: citizen_user
--

CREATE SEQUENCE data_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE data_id_seq OWNER TO citizen_user;

--
-- Name: dl_series_master; Type: TABLE; Schema: public; Owner: citizen_user; Tablespace: 
--

CREATE TABLE dl_series_master (
    dl_series_id bigint NOT NULL,
    created_by character varying(50),
    created_on date,
    modified_by character varying(50),
    modified_on date,
    end_number integer,
    start_number integer,
    use_number integer,
    year integer
);


ALTER TABLE dl_series_master OWNER TO citizen_user;

--
-- Name: dl_series_seq; Type: SEQUENCE; Schema: public; Owner: citizen_user
--

CREATE SEQUENCE dl_series_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE dl_series_seq OWNER TO citizen_user;

--
-- Name: document_master; Type: TABLE; Schema: public; Owner: citizen_user; Tablespace: 
--

CREATE TABLE document_master (
    document_id integer NOT NULL,
    created_by character varying(50),
    created_on bigint,
    modified_by character varying(50),
    modified_on bigint,
    status boolean,
    description character varying(100),
    user_role character varying(100)
);


ALTER TABLE document_master OWNER TO citizen_user;

--
-- Name: document_master_seq; Type: SEQUENCE; Schema: public; Owner: citizen_user
--

CREATE SEQUENCE document_master_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE document_master_seq OWNER TO citizen_user;

--
-- Name: event; Type: TABLE; Schema: public; Owner: citizen_user; Tablespace: 
--

CREATE TABLE event (
    event_id bigint NOT NULL,
    created_by character varying(50),
    created_on bigint,
    modified_by character varying(50),
    modified_on bigint,
    application_id bigint,
    attachement character varying(255),
    email_notify boolean,
    event_type character varying(255),
    iteration integer,
    service_type character varying(255),
    sms_notify boolean
);


ALTER TABLE event OWNER TO citizen_user;

--
-- Name: event_id_seq; Type: SEQUENCE; Schema: public; Owner: citizen_user
--

CREATE SEQUENCE event_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE event_id_seq OWNER TO citizen_user;

--
-- Name: fee_dtl_seq; Type: SEQUENCE; Schema: public; Owner: citizen_user
--

CREATE SEQUENCE fee_dtl_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE fee_dtl_seq OWNER TO citizen_user;

--
-- Name: holiday_seq; Type: SEQUENCE; Schema: public; Owner: citizen_user
--

CREATE SEQUENCE holiday_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE holiday_seq OWNER TO citizen_user;

--
-- Name: holidays; Type: TABLE; Schema: public; Owner: citizen_user; Tablespace: 
--

CREATE TABLE holidays (
    holiday_id bigint NOT NULL,
    created_by character varying(50),
    created_on bigint,
    modified_by character varying(50),
    modified_on bigint,
    date bigint,
    is_enabled boolean
);


ALTER TABLE holidays OWNER TO citizen_user;

--
-- Name: license_holder_approved_details; Type: TABLE; Schema: public; Owner: citizen_user; Tablespace: 
--

CREATE TABLE license_holder_approved_details (
    license_holder_approved_details_id bigint NOT NULL,
    created_by character varying(50),
    created_on bigint,
    modified_by character varying(50),
    modified_on bigint,
    first_name character varying(255),
    guardian_name character varying(255),
    last_name character varying(255),
    application_id bigint NOT NULL
);


ALTER TABLE license_holder_approved_details OWNER TO citizen_user;

--
-- Name: license_holder_approved_details_seq; Type: SEQUENCE; Schema: public; Owner: citizen_user
--

CREATE SEQUENCE license_holder_approved_details_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE license_holder_approved_details_seq OWNER TO citizen_user;

--
-- Name: license_permit_details; Type: TABLE; Schema: public; Owner: citizen_user; Tablespace: 
--

CREATE TABLE license_permit_details (
    license_permit_details_id bigint NOT NULL,
    created_by character varying(50),
    created_on date,
    modified_by character varying(50),
    modified_on date,
    app_exam_number character varying(255),
    is_badge boolean DEFAULT false,
    license_number character varying(255),
    license_type character varying(255),
    parent_consent_aadhaar_no character varying(255),
    status integer,
    test_date date,
    test_exempted character(1),
    test_exempted_reason character varying(255),
    test_marks character varying(255),
    test_no_of_attemp integer,
    test_result character varying(255),
    vehicle_class_code character varying(255),
    application_id bigint NOT NULL
);


ALTER TABLE license_permit_details OWNER TO citizen_user;

--
-- Name: license_permit_details_history; Type: TABLE; Schema: public; Owner: citizen_user; Tablespace: 
--

CREATE TABLE license_permit_details_history (
    license_permit_details_history_id bigint NOT NULL,
    created_by character varying(50),
    created_on date,
    modified_by character varying(50),
    modified_on date,
    status integer,
    user_id bigint,
    vehicle_class_code character varying(255),
    application_id bigint NOT NULL
);


ALTER TABLE license_permit_details_history OWNER TO citizen_user;

--
-- Name: license_permit_details_history_seq; Type: SEQUENCE; Schema: public; Owner: citizen_user
--

CREATE SEQUENCE license_permit_details_history_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE license_permit_details_history_seq OWNER TO citizen_user;

--
-- Name: license_permit_details_seq; Type: SEQUENCE; Schema: public; Owner: citizen_user
--

CREATE SEQUENCE license_permit_details_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE license_permit_details_seq OWNER TO citizen_user;

--
-- Name: login_attempt; Type: TABLE; Schema: public; Owner: citizen_user; Tablespace: 
--

CREATE TABLE login_attempt (
    login_attempt_id bigint NOT NULL,
    created_by character varying(50),
    created_on bigint,
    modified_by character varying(50),
    modified_on bigint,
    aadhar_number character varying(255),
    key_type character varying(255),
    login_count integer,
    login_time bigint,
    unique_key character varying(255)
);


ALTER TABLE login_attempt OWNER TO citizen_user;

--
-- Name: login_attempt_seq; Type: SEQUENCE; Schema: public; Owner: citizen_user
--

CREATE SEQUENCE login_attempt_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE login_attempt_seq OWNER TO citizen_user;

--
-- Name: office_ipaddress_seq; Type: SEQUENCE; Schema: public; Owner: citizen_user
--

CREATE SEQUENCE office_ipaddress_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE office_ipaddress_seq OWNER TO citizen_user;

--
-- Name: ot_token; Type: TABLE; Schema: public; Owner: citizen_user; Tablespace: 
--

CREATE TABLE ot_token (
    token_id bigint NOT NULL,
    created_by character varying(50),
    created_on bigint,
    modified_by character varying(50),
    modified_on bigint,
    claimant_aadhaar_number character varying(255),
    claimant_ip character varying(50),
    claimant_name character varying(255),
    generator_aadhaar_number character varying(255),
    generator_ip character varying(50),
    generator_name character varying(255),
    is_claimed boolean DEFAULT false,
    ownership_type integer,
    token_number character varying(255),
    application_id bigint
);


ALTER TABLE ot_token OWNER TO citizen_user;

--
-- Name: ot_token_seq; Type: SEQUENCE; Schema: public; Owner: citizen_user
--

CREATE SEQUENCE ot_token_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ot_token_seq OWNER TO citizen_user;

--
-- Name: pending_username; Type: TABLE; Schema: public; Owner: citizen_user; Tablespace: 
--

CREATE TABLE pending_username (
    pending_username_id bigint NOT NULL,
    created_by character varying(50),
    created_on bigint,
    modified_by character varying(50),
    modified_on bigint,
    status integer,
    username character varying(255),
    application_id bigint NOT NULL
);


ALTER TABLE pending_username OWNER TO citizen_user;

--
-- Name: pending_username_seq; Type: SEQUENCE; Schema: public; Owner: citizen_user
--

CREATE SEQUENCE pending_username_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE pending_username_seq OWNER TO citizen_user;

--
-- Name: question_options; Type: TABLE; Schema: public; Owner: citizen_user; Tablespace: 
--

CREATE TABLE question_options (
    question_options_id bigint NOT NULL,
    created_by character varying(50),
    created_on bigint,
    modified_by character varying(50),
    modified_on bigint,
    is_correct boolean,
    is_mandatory boolean DEFAULT false,
    option character varying(255),
    status integer,
    question_id bigint
);


ALTER TABLE question_options OWNER TO citizen_user;

--
-- Name: question_options_seq; Type: SEQUENCE; Schema: public; Owner: citizen_user
--

CREATE SEQUENCE question_options_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE question_options_seq OWNER TO citizen_user;

--
-- Name: questionnaire_feedback; Type: TABLE; Schema: public; Owner: citizen_user; Tablespace: 
--

CREATE TABLE questionnaire_feedback (
    questionnaire_feedback_id bigint NOT NULL,
    created_by character varying(50),
    created_on bigint,
    modified_by character varying(50),
    modified_on bigint,
    answer character varying(255),
    is_correct boolean,
    question character varying(255),
    status integer,
    test_type character varying(255),
    application_id bigint
);


ALTER TABLE questionnaire_feedback OWNER TO citizen_user;

--
-- Name: questionnaire_feedback_seq; Type: SEQUENCE; Schema: public; Owner: citizen_user
--

CREATE SEQUENCE questionnaire_feedback_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE questionnaire_feedback_seq OWNER TO citizen_user;

--
-- Name: questions; Type: TABLE; Schema: public; Owner: citizen_user; Tablespace: 
--

CREATE TABLE questions (
    question_id bigint NOT NULL,
    created_by character varying(50),
    created_on bigint,
    modified_by character varying(50),
    modified_on bigint,
    question character varying(255),
    status integer,
    test_type character varying(255),
    vehicle_class character varying(255)
);


ALTER TABLE questions OWNER TO citizen_user;

--
-- Name: questions_seq; Type: SEQUENCE; Schema: public; Owner: citizen_user
--

CREATE SEQUENCE questions_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE questions_seq OWNER TO citizen_user;

--
-- Name: rejected_app_removed_exe_id_hist; Type: TABLE; Schema: public; Owner: citizen_user; Tablespace: 
--

CREATE TABLE rejected_app_removed_exe_id_hist (
    id bigint NOT NULL,
    created_by character varying(50),
    created_on bigint,
    modified_by character varying(50),
    modified_on bigint,
    app_number character varying(255),
    execution_id character varying(255),
    iteration integer,
    service_code character varying(255)
);


ALTER TABLE rejected_app_removed_exe_id_hist OWNER TO citizen_user;

--
-- Name: rta_office_ipaddress; Type: TABLE; Schema: public; Owner: citizen_user; Tablespace: 
--

CREATE TABLE rta_office_ipaddress (
    office_ipaddress_id bigint NOT NULL,
    created_by character varying(50),
    created_on date,
    modified_by character varying(50),
    modified_on date,
    ipaddress character varying(50),
    office_code character varying(255),
    remarks character varying(255),
    status character varying(10)
);


ALTER TABLE rta_office_ipaddress OWNER TO citizen_user;

--
-- Name: rta_office_schedule; Type: TABLE; Schema: public; Owner: citizen_user; Tablespace: 
--

CREATE TABLE rta_office_schedule (
    rta_office_schedule_id bigint NOT NULL,
    created_by character varying(50),
    created_on bigint,
    modified_by character varying(50),
    modified_on bigint,
    end_time bigint,
    is_enabled boolean,
    no_of_simul_slots integer,
    rta_office_code character varying(255),
    service_category character varying(20),
    start_time bigint
);


ALTER TABLE rta_office_schedule OWNER TO citizen_user;

--
-- Name: rta_office_schedule_seq; Type: SEQUENCE; Schema: public; Owner: citizen_user
--

CREATE SEQUENCE rta_office_schedule_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE rta_office_schedule_seq OWNER TO citizen_user;

--
-- Name: rta_office_test_config; Type: TABLE; Schema: public; Owner: citizen_user; Tablespace: 
--

CREATE TABLE rta_office_test_config (
    rta_office_test_config_id bigint NOT NULL,
    created_by character varying(50),
    created_on bigint,
    modified_by character varying(50),
    modified_on bigint,
    duration bigint,
    is_enabled boolean,
    simul_app_count integer,
    slot_service_type character varying(255),
    rta_office_schedule_id bigint
);


ALTER TABLE rta_office_test_config OWNER TO citizen_user;

--
-- Name: rta_office_test_config_seq; Type: SEQUENCE; Schema: public; Owner: citizen_user
--

CREATE SEQUENCE rta_office_test_config_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE rta_office_test_config_seq OWNER TO citizen_user;

--
-- Name: service_master; Type: TABLE; Schema: public; Owner: citizen_user; Tablespace: 
--

CREATE TABLE service_master (
    service_id bigint NOT NULL,
    created_by character varying(50),
    created_on bigint,
    modified_by character varying(50),
    modified_on bigint,
    code character varying(255),
    name character varying(255),
    status boolean,
    service_category character varying(20),
    slot_applicable boolean
);


ALTER TABLE service_master OWNER TO citizen_user;

--
-- Name: service_seq; Type: SEQUENCE; Schema: public; Owner: citizen_user
--

CREATE SEQUENCE service_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE service_seq OWNER TO citizen_user;

--
-- Name: session_seq; Type: SEQUENCE; Schema: public; Owner: citizen_user
--

CREATE SEQUENCE session_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE session_seq OWNER TO citizen_user;

--
-- Name: slot_applications; Type: TABLE; Schema: public; Owner: citizen_user; Tablespace: 
--

CREATE TABLE slot_applications (
    slot_applications_id bigint NOT NULL,
    created_by character varying(50),
    created_on bigint,
    modified_by character varying(50),
    modified_on bigint,
    approval_status integer,
    expiry_time bigint,
    iteration integer DEFAULT 1,
    service_code character varying(255),
    slot_service_type character varying(255),
    slot_status character varying(255),
    application_id bigint,
    slot_id bigint
);


ALTER TABLE slot_applications OWNER TO citizen_user;

--
-- Name: slot_applications_seq; Type: SEQUENCE; Schema: public; Owner: citizen_user
--

CREATE SEQUENCE slot_applications_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE slot_applications_seq OWNER TO citizen_user;

--
-- Name: slots; Type: TABLE; Schema: public; Owner: citizen_user; Tablespace: 
--

CREATE TABLE slots (
    slot_id bigint NOT NULL,
    created_by character varying(50),
    created_on bigint,
    modified_by character varying(50),
    modified_on bigint,
    application_count integer,
    duration bigint,
    end_time bigint,
    is_completed boolean,
    rta_office_code character varying(255),
    scheduled_date bigint,
    scheduled_time bigint,
    service_category character varying(20),
    service_code character varying(255),
    slot_service_type character varying(255),
    start_time bigint
);


ALTER TABLE slots OWNER TO citizen_user;

--
-- Name: slots_seq; Type: SEQUENCE; Schema: public; Owner: citizen_user
--

CREATE SEQUENCE slots_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE slots_seq OWNER TO citizen_user;

--
-- Name: tax_dtl_seq; Type: SEQUENCE; Schema: public; Owner: citizen_user
--

CREATE SEQUENCE tax_dtl_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE tax_dtl_seq OWNER TO citizen_user;

--
-- Name: tax_type; Type: TABLE; Schema: public; Owner: citizen_user; Tablespace: 
--

CREATE TABLE tax_type (
    tax_type_id bigint NOT NULL,
    created_by character varying(50),
    created_on bigint,
    modified_by character varying(50),
    modified_on bigint,
    code character varying(255),
    name character varying(255),
    status boolean
);


ALTER TABLE tax_type OWNER TO citizen_user;

--
-- Name: tax_type_seq; Type: SEQUENCE; Schema: public; Owner: citizen_user
--

CREATE SEQUENCE tax_type_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE tax_type_seq OWNER TO citizen_user;

--
-- Name: tran_dtl_seq; Type: SEQUENCE; Schema: public; Owner: citizen_user
--

CREATE SEQUENCE tran_dtl_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE tran_dtl_seq OWNER TO citizen_user;

--
-- Name: trans_hist_seq; Type: SEQUENCE; Schema: public; Owner: citizen_user
--

CREATE SEQUENCE trans_hist_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE trans_hist_seq OWNER TO citizen_user;

--
-- Name: transaction_history; Type: TABLE; Schema: public; Owner: citizen_user; Tablespace: 
--

CREATE TABLE transaction_history (
    trans_hist_id bigint NOT NULL,
    created_by character varying(50),
    created_on bigint,
    modified_by character varying(50),
    modified_on bigint,
    application_id bigint,
    payment_type integer,
    request_parameter text,
    response_parameter text,
    service_type integer,
    status integer NOT NULL,
    transaction_no character varying(255),
    transaction_detail_id bigint
);


ALTER TABLE transaction_history OWNER TO citizen_user;

--
-- Name: user_sessions; Type: TABLE; Schema: public; Owner: citizen_user; Tablespace: 
--

CREATE TABLE user_sessions (
    session_id bigint NOT NULL,
    created_by character varying(50),
    created_on bigint,
    modified_by character varying(50),
    modified_on bigint,
    aadhar_number character varying(255),
    completion_status integer,
    key_type character varying(255),
    login_time bigint,
    service_code character varying(255),
    unique_key character varying(255),
    vehicle_rc_id bigint
);


ALTER TABLE user_sessions OWNER TO citizen_user;

--
-- Name: vehicle_class_mst; Type: TABLE; Schema: public; Owner: citizen_user; Tablespace: 
--

CREATE TABLE vehicle_class_mst (
    vehicle_class character varying(255) NOT NULL,
    created_by character varying(50),
    created_on bigint,
    modified_by character varying(50),
    modified_on bigint,
    is_active character varying(255),
    vehicle_class_description character varying(255),
    vehicle_class_group character varying(255),
    vehicle_class_type character varying(255),
    vehicle_transport_type character varying(255)
);


ALTER TABLE vehicle_class_mst OWNER TO citizen_user;

--
-- Name: vehicle_class_ref; Type: TABLE; Schema: public; Owner: citizen_user; Tablespace: 
--

CREATE TABLE vehicle_class_ref (
    vehicle_class_id bigint NOT NULL,
    created_by character varying(50),
    created_on bigint,
    modified_by character varying(50),
    modified_on bigint,
    age_group_cd character varying(255),
    badge_available boolean,
    hazardous boolean,
    idp_class character varying(50),
    licence_class_type character varying(255),
    max_age integer,
    requires_doctor_cert boolean,
    validity_period integer,
    vehicle_class character varying(50)
);


ALTER TABLE vehicle_class_ref OWNER TO citizen_user;

--
-- Name: vehicle_class_ref_seq; Type: SEQUENCE; Schema: public; Owner: citizen_user
--

CREATE SEQUENCE vehicle_class_ref_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE vehicle_class_ref_seq OWNER TO citizen_user;

--
-- Name: vehicle_class_tests_mst; Type: TABLE; Schema: public; Owner: citizen_user; Tablespace: 
--

CREATE TABLE vehicle_class_tests_mst (
    vehicle_class_tests_id bigint NOT NULL,
    created_by character varying(50),
    created_on bigint,
    modified_by character varying(50),
    modified_on bigint,
    test_type character varying(255),
    vehicle_class character varying(255)
);


ALTER TABLE vehicle_class_tests_mst OWNER TO citizen_user;

--
-- Name: vehicle_class_tests_seq; Type: SEQUENCE; Schema: public; Owner: citizen_user
--

CREATE SEQUENCE vehicle_class_tests_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE vehicle_class_tests_seq OWNER TO citizen_user;

--
-- Name: vehicle_inspection; Type: TABLE; Schema: public; Owner: citizen_user; Tablespace: 
--

CREATE TABLE vehicle_inspection (
    vehicle_inspection_id bigint NOT NULL,
    created_by character varying(50),
    created_on bigint,
    modified_by character varying(50),
    modified_on bigint,
    inspection_date bigint,
    inspection_status integer DEFAULT 8 NOT NULL,
    revocation_status integer DEFAULT 2 NOT NULL,
    schedule_inspection_date bigint,
    user_id bigint,
    application_id bigint NOT NULL
);


ALTER TABLE vehicle_inspection OWNER TO citizen_user;

--
-- Name: vehicle_inspection_seq; Type: SEQUENCE; Schema: public; Owner: citizen_user
--

CREATE SEQUENCE vehicle_inspection_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE vehicle_inspection_seq OWNER TO citizen_user;

--
-- Name: age_group_ref_pkey; Type: CONSTRAINT; Schema: public; Owner: citizen_user; Tablespace: 
--

ALTER TABLE ONLY age_group_ref
    ADD CONSTRAINT age_group_ref_pkey PRIMARY KEY (age_group_cd);


--
-- Name: application_approval_history_pkey; Type: CONSTRAINT; Schema: public; Owner: citizen_user; Tablespace: 
--

ALTER TABLE ONLY application_approval_history
    ADD CONSTRAINT application_approval_history_pkey PRIMARY KEY (app_history_id);


--
-- Name: application_attachments_pkey; Type: CONSTRAINT; Schema: public; Owner: citizen_user; Tablespace: 
--

ALTER TABLE ONLY application_attachments
    ADD CONSTRAINT application_attachments_pkey PRIMARY KEY (attachment_id);


--
-- Name: application_bank_transaction_detail_pkey; Type: CONSTRAINT; Schema: public; Owner: citizen_user; Tablespace: 
--

ALTER TABLE ONLY application_bank_transaction_detail
    ADD CONSTRAINT application_bank_transaction_detail_pkey PRIMARY KEY (transaction_id);


--
-- Name: application_fee_detail_pkey; Type: CONSTRAINT; Schema: public; Owner: citizen_user; Tablespace: 
--

ALTER TABLE ONLY application_fee_detail
    ADD CONSTRAINT application_fee_detail_pkey PRIMARY KEY (fee_dtl_id);


--
-- Name: application_form_data_pkey; Type: CONSTRAINT; Schema: public; Owner: citizen_user; Tablespace: 
--

ALTER TABLE ONLY application_form_data
    ADD CONSTRAINT application_form_data_pkey PRIMARY KEY (data_id);


--
-- Name: application_pkey; Type: CONSTRAINT; Schema: public; Owner: citizen_user; Tablespace: 
--

ALTER TABLE ONLY application
    ADD CONSTRAINT application_pkey PRIMARY KEY (application_id);


--
-- Name: application_tax_detail_pkey; Type: CONSTRAINT; Schema: public; Owner: citizen_user; Tablespace: 
--

ALTER TABLE ONLY application_tax_detail
    ADD CONSTRAINT application_tax_detail_pkey PRIMARY KEY (tax_dtl_id);


--
-- Name: citizen_address_outside_ap_pkey; Type: CONSTRAINT; Schema: public; Owner: citizen_user; Tablespace: 
--

ALTER TABLE ONLY citizen_address_outside_ap
    ADD CONSTRAINT citizen_address_outside_ap_pkey PRIMARY KEY (address_id);


--
-- Name: citizen_address_pkey; Type: CONSTRAINT; Schema: public; Owner: citizen_user; Tablespace: 
--

ALTER TABLE ONLY citizen_address
    ADD CONSTRAINT citizen_address_pkey PRIMARY KEY (address_id);


--
-- Name: citizen_info_pkey; Type: CONSTRAINT; Schema: public; Owner: citizen_user; Tablespace: 
--

ALTER TABLE ONLY citizen_info
    ADD CONSTRAINT citizen_info_pkey PRIMARY KEY (citizen_info_id);


--
-- Name: citizen_invoice_pkey; Type: CONSTRAINT; Schema: public; Owner: citizen_user; Tablespace: 
--

ALTER TABLE ONLY citizen_invoice
    ADD CONSTRAINT citizen_invoice_pkey PRIMARY KEY (citizen_invc_id);


--
-- Name: dl_series_master_pkey; Type: CONSTRAINT; Schema: public; Owner: citizen_user; Tablespace: 
--

ALTER TABLE ONLY dl_series_master
    ADD CONSTRAINT dl_series_master_pkey PRIMARY KEY (dl_series_id);


--
-- Name: document_master_pkey; Type: CONSTRAINT; Schema: public; Owner: citizen_user; Tablespace: 
--

ALTER TABLE ONLY document_master
    ADD CONSTRAINT document_master_pkey PRIMARY KEY (document_id);


--
-- Name: event_pkey; Type: CONSTRAINT; Schema: public; Owner: citizen_user; Tablespace: 
--

ALTER TABLE ONLY event
    ADD CONSTRAINT event_pkey PRIMARY KEY (event_id);


--
-- Name: holidays_pkey; Type: CONSTRAINT; Schema: public; Owner: citizen_user; Tablespace: 
--

ALTER TABLE ONLY holidays
    ADD CONSTRAINT holidays_pkey PRIMARY KEY (holiday_id);


--
-- Name: license_holder_approved_details_pkey; Type: CONSTRAINT; Schema: public; Owner: citizen_user; Tablespace: 
--

ALTER TABLE ONLY license_holder_approved_details
    ADD CONSTRAINT license_holder_approved_details_pkey PRIMARY KEY (license_holder_approved_details_id);


--
-- Name: license_permit_details_history_pkey; Type: CONSTRAINT; Schema: public; Owner: citizen_user; Tablespace: 
--

ALTER TABLE ONLY license_permit_details_history
    ADD CONSTRAINT license_permit_details_history_pkey PRIMARY KEY (license_permit_details_history_id);


--
-- Name: license_permit_details_pkey; Type: CONSTRAINT; Schema: public; Owner: citizen_user; Tablespace: 
--

ALTER TABLE ONLY license_permit_details
    ADD CONSTRAINT license_permit_details_pkey PRIMARY KEY (license_permit_details_id);


--
-- Name: login_attempt_pkey; Type: CONSTRAINT; Schema: public; Owner: citizen_user; Tablespace: 
--

ALTER TABLE ONLY login_attempt
    ADD CONSTRAINT login_attempt_pkey PRIMARY KEY (login_attempt_id);


--
-- Name: ot_token_pkey; Type: CONSTRAINT; Schema: public; Owner: citizen_user; Tablespace: 
--

ALTER TABLE ONLY ot_token
    ADD CONSTRAINT ot_token_pkey PRIMARY KEY (token_id);


--
-- Name: pending_username_pkey; Type: CONSTRAINT; Schema: public; Owner: citizen_user; Tablespace: 
--

ALTER TABLE ONLY pending_username
    ADD CONSTRAINT pending_username_pkey PRIMARY KEY (pending_username_id);


--
-- Name: question_options_pkey; Type: CONSTRAINT; Schema: public; Owner: citizen_user; Tablespace: 
--

ALTER TABLE ONLY question_options
    ADD CONSTRAINT question_options_pkey PRIMARY KEY (question_options_id);


--
-- Name: questionnaire_feedback_pkey; Type: CONSTRAINT; Schema: public; Owner: citizen_user; Tablespace: 
--

ALTER TABLE ONLY questionnaire_feedback
    ADD CONSTRAINT questionnaire_feedback_pkey PRIMARY KEY (questionnaire_feedback_id);


--
-- Name: questions_pkey; Type: CONSTRAINT; Schema: public; Owner: citizen_user; Tablespace: 
--

ALTER TABLE ONLY questions
    ADD CONSTRAINT questions_pkey PRIMARY KEY (question_id);


--
-- Name: rejected_app_removed_exe_id_hist_pkey; Type: CONSTRAINT; Schema: public; Owner: citizen_user; Tablespace: 
--

ALTER TABLE ONLY rejected_app_removed_exe_id_hist
    ADD CONSTRAINT rejected_app_removed_exe_id_hist_pkey PRIMARY KEY (id);


--
-- Name: rta_office_ipaddress_pkey; Type: CONSTRAINT; Schema: public; Owner: citizen_user; Tablespace: 
--

ALTER TABLE ONLY rta_office_ipaddress
    ADD CONSTRAINT rta_office_ipaddress_pkey PRIMARY KEY (office_ipaddress_id);


--
-- Name: rta_office_schedule_pkey; Type: CONSTRAINT; Schema: public; Owner: citizen_user; Tablespace: 
--

ALTER TABLE ONLY rta_office_schedule
    ADD CONSTRAINT rta_office_schedule_pkey PRIMARY KEY (rta_office_schedule_id);


--
-- Name: rta_office_test_config_pkey; Type: CONSTRAINT; Schema: public; Owner: citizen_user; Tablespace: 
--

ALTER TABLE ONLY rta_office_test_config
    ADD CONSTRAINT rta_office_test_config_pkey PRIMARY KEY (rta_office_test_config_id);


--
-- Name: service_master_pkey; Type: CONSTRAINT; Schema: public; Owner: citizen_user; Tablespace: 
--

ALTER TABLE ONLY service_master
    ADD CONSTRAINT service_master_pkey PRIMARY KEY (service_id);


--
-- Name: slot_applications_pkey; Type: CONSTRAINT; Schema: public; Owner: citizen_user; Tablespace: 
--

ALTER TABLE ONLY slot_applications
    ADD CONSTRAINT slot_applications_pkey PRIMARY KEY (slot_applications_id);


--
-- Name: slots_pkey; Type: CONSTRAINT; Schema: public; Owner: citizen_user; Tablespace: 
--

ALTER TABLE ONLY slots
    ADD CONSTRAINT slots_pkey PRIMARY KEY (slot_id);


--
-- Name: tax_type_pkey; Type: CONSTRAINT; Schema: public; Owner: citizen_user; Tablespace: 
--

ALTER TABLE ONLY tax_type
    ADD CONSTRAINT tax_type_pkey PRIMARY KEY (tax_type_id);


--
-- Name: transaction_history_pkey; Type: CONSTRAINT; Schema: public; Owner: citizen_user; Tablespace: 
--

ALTER TABLE ONLY transaction_history
    ADD CONSTRAINT transaction_history_pkey PRIMARY KEY (trans_hist_id);


--
-- Name: uk_j2vu5jq6jvx2y9ls228trlkgl; Type: CONSTRAINT; Schema: public; Owner: citizen_user; Tablespace: 
--

ALTER TABLE ONLY pending_username
    ADD CONSTRAINT uk_j2vu5jq6jvx2y9ls228trlkgl UNIQUE (username);


--
-- Name: uk_p4jkm836xjnhfcx4pg9324awt; Type: CONSTRAINT; Schema: public; Owner: citizen_user; Tablespace: 
--

ALTER TABLE ONLY slots
    ADD CONSTRAINT uk_p4jkm836xjnhfcx4pg9324awt UNIQUE (start_time, scheduled_date, rta_office_code, slot_service_type, service_category);


--
-- Name: uk_pmjyvju8u7x72yc818n227mvo; Type: CONSTRAINT; Schema: public; Owner: citizen_user; Tablespace: 
--

ALTER TABLE ONLY pending_username
    ADD CONSTRAINT uk_pmjyvju8u7x72yc818n227mvo UNIQUE (application_id);


--
-- Name: uk_q301177xp04tcw23nnau146hk; Type: CONSTRAINT; Schema: public; Owner: citizen_user; Tablespace: 
--

ALTER TABLE ONLY application_bank_transaction_detail
    ADD CONSTRAINT uk_q301177xp04tcw23nnau146hk UNIQUE (bank_transac_no);


--
-- Name: user_sessions_pkey; Type: CONSTRAINT; Schema: public; Owner: citizen_user; Tablespace: 
--

ALTER TABLE ONLY user_sessions
    ADD CONSTRAINT user_sessions_pkey PRIMARY KEY (session_id);


--
-- Name: vehicle_class_mst_pkey; Type: CONSTRAINT; Schema: public; Owner: citizen_user; Tablespace: 
--

ALTER TABLE ONLY vehicle_class_mst
    ADD CONSTRAINT vehicle_class_mst_pkey PRIMARY KEY (vehicle_class);


--
-- Name: vehicle_class_ref_pkey; Type: CONSTRAINT; Schema: public; Owner: citizen_user; Tablespace: 
--

ALTER TABLE ONLY vehicle_class_ref
    ADD CONSTRAINT vehicle_class_ref_pkey PRIMARY KEY (vehicle_class_id);


--
-- Name: vehicle_class_tests_mst_pkey; Type: CONSTRAINT; Schema: public; Owner: citizen_user; Tablespace: 
--

ALTER TABLE ONLY vehicle_class_tests_mst
    ADD CONSTRAINT vehicle_class_tests_mst_pkey PRIMARY KEY (vehicle_class_tests_id);


--
-- Name: vehicle_inspection_pkey; Type: CONSTRAINT; Schema: public; Owner: citizen_user; Tablespace: 
--

ALTER TABLE ONLY vehicle_inspection
    ADD CONSTRAINT vehicle_inspection_pkey PRIMARY KEY (vehicle_inspection_id);


--
-- Name: fk_1sshdja6e0eckcqijr5hprair; Type: FK CONSTRAINT; Schema: public; Owner: citizen_user
--

ALTER TABLE ONLY application_attachments
    ADD CONSTRAINT fk_1sshdja6e0eckcqijr5hprair FOREIGN KEY (document_id) REFERENCES document_master(document_id);


--
-- Name: fk_38one40e0j8majc57bs6bx2dt; Type: FK CONSTRAINT; Schema: public; Owner: citizen_user
--

ALTER TABLE ONLY citizen_invoice
    ADD CONSTRAINT fk_38one40e0j8majc57bs6bx2dt FOREIGN KEY (fee_dtl_id) REFERENCES application_fee_detail(fee_dtl_id);


--
-- Name: fk_4jfwv8cjwd7rhe5h2h2oyadmc; Type: FK CONSTRAINT; Schema: public; Owner: citizen_user
--

ALTER TABLE ONLY application_bank_transaction_detail
    ADD CONSTRAINT fk_4jfwv8cjwd7rhe5h2h2oyadmc FOREIGN KEY (application_id) REFERENCES application(application_id);


--
-- Name: fk_6l9fhsk0ggt8ica721nmpvgqd; Type: FK CONSTRAINT; Schema: public; Owner: citizen_user
--

ALTER TABLE ONLY vehicle_inspection
    ADD CONSTRAINT fk_6l9fhsk0ggt8ica721nmpvgqd FOREIGN KEY (application_id) REFERENCES application(application_id);


--
-- Name: fk_6sn2plkxmfusirwcgnq7ixre9; Type: FK CONSTRAINT; Schema: public; Owner: citizen_user
--

ALTER TABLE ONLY application_tax_detail
    ADD CONSTRAINT fk_6sn2plkxmfusirwcgnq7ixre9 FOREIGN KEY (application_id) REFERENCES application(application_id);


--
-- Name: fk_7f9m6u6e6exrx43blsmmytq2; Type: FK CONSTRAINT; Schema: public; Owner: citizen_user
--

ALTER TABLE ONLY application_attachments
    ADD CONSTRAINT fk_7f9m6u6e6exrx43blsmmytq2 FOREIGN KEY (application_id) REFERENCES application(application_id);


--
-- Name: fk_80789x895y2rw9ep44343leur; Type: FK CONSTRAINT; Schema: public; Owner: citizen_user
--

ALTER TABLE ONLY slot_applications
    ADD CONSTRAINT fk_80789x895y2rw9ep44343leur FOREIGN KEY (application_id) REFERENCES application(application_id);


--
-- Name: fk_9ow7cvpax9i55eabu45bwws5j; Type: FK CONSTRAINT; Schema: public; Owner: citizen_user
--

ALTER TABLE ONLY application_form_data
    ADD CONSTRAINT fk_9ow7cvpax9i55eabu45bwws5j FOREIGN KEY (application_id) REFERENCES application(application_id);


--
-- Name: fk_a6iu65y3cy6wpk7130raoovnk; Type: FK CONSTRAINT; Schema: public; Owner: citizen_user
--

ALTER TABLE ONLY questionnaire_feedback
    ADD CONSTRAINT fk_a6iu65y3cy6wpk7130raoovnk FOREIGN KEY (application_id) REFERENCES application(application_id);


--
-- Name: fk_au1a1oj92mo88w3biw7md3jtn; Type: FK CONSTRAINT; Schema: public; Owner: citizen_user
--

ALTER TABLE ONLY citizen_address_outside_ap
    ADD CONSTRAINT fk_au1a1oj92mo88w3biw7md3jtn FOREIGN KEY (application_id) REFERENCES application(application_id);


--
-- Name: fk_bo1kh230i39ckwf4s63t5blu7; Type: FK CONSTRAINT; Schema: public; Owner: citizen_user
--

ALTER TABLE ONLY citizen_address
    ADD CONSTRAINT fk_bo1kh230i39ckwf4s63t5blu7 FOREIGN KEY (application_id) REFERENCES application(application_id);


--
-- Name: fk_d9y6vca8mar1qm2ytr5m3gxyx; Type: FK CONSTRAINT; Schema: public; Owner: citizen_user
--

ALTER TABLE ONLY license_permit_details_history
    ADD CONSTRAINT fk_d9y6vca8mar1qm2ytr5m3gxyx FOREIGN KEY (application_id) REFERENCES application(application_id);


--
-- Name: fk_degt87nkjq9hg8uedqxqtlsee; Type: FK CONSTRAINT; Schema: public; Owner: citizen_user
--

ALTER TABLE ONLY question_options
    ADD CONSTRAINT fk_degt87nkjq9hg8uedqxqtlsee FOREIGN KEY (question_id) REFERENCES questions(question_id);


--
-- Name: fk_f05gqohhuixplg0hfk5k654pd; Type: FK CONSTRAINT; Schema: public; Owner: citizen_user
--

ALTER TABLE ONLY transaction_history
    ADD CONSTRAINT fk_f05gqohhuixplg0hfk5k654pd FOREIGN KEY (transaction_detail_id) REFERENCES application_bank_transaction_detail(transaction_id);


--
-- Name: fk_j1b13kn68i29614cgry9aby0s; Type: FK CONSTRAINT; Schema: public; Owner: citizen_user
--

ALTER TABLE ONLY ot_token
    ADD CONSTRAINT fk_j1b13kn68i29614cgry9aby0s FOREIGN KEY (application_id) REFERENCES application(application_id);


--
-- Name: fk_ks98mvndyiyvatcs5mh46jx4a; Type: FK CONSTRAINT; Schema: public; Owner: citizen_user
--

ALTER TABLE ONLY slot_applications
    ADD CONSTRAINT fk_ks98mvndyiyvatcs5mh46jx4a FOREIGN KEY (slot_id) REFERENCES slots(slot_id);


--
-- Name: fk_ofrid8jot3bwakca0qnffyxit; Type: FK CONSTRAINT; Schema: public; Owner: citizen_user
--

ALTER TABLE ONLY application_fee_detail
    ADD CONSTRAINT fk_ofrid8jot3bwakca0qnffyxit FOREIGN KEY (application_id) REFERENCES application(application_id);


--
-- Name: fk_pjjmtqa6sv78evxnh2fq6n80x; Type: FK CONSTRAINT; Schema: public; Owner: citizen_user
--

ALTER TABLE ONLY license_permit_details
    ADD CONSTRAINT fk_pjjmtqa6sv78evxnh2fq6n80x FOREIGN KEY (application_id) REFERENCES application(application_id);


--
-- Name: fk_pmjyvju8u7x72yc818n227mvo; Type: FK CONSTRAINT; Schema: public; Owner: citizen_user
--

ALTER TABLE ONLY pending_username
    ADD CONSTRAINT fk_pmjyvju8u7x72yc818n227mvo FOREIGN KEY (application_id) REFERENCES application(application_id);


--
-- Name: fk_qbsc0magj50ivyi2alaakxxqq; Type: FK CONSTRAINT; Schema: public; Owner: citizen_user
--

ALTER TABLE ONLY citizen_invoice
    ADD CONSTRAINT fk_qbsc0magj50ivyi2alaakxxqq FOREIGN KEY (tax_dtl_id) REFERENCES application_tax_detail(tax_dtl_id);


--
-- Name: fk_qemv0tcj1wwnreplb19dt24aq; Type: FK CONSTRAINT; Schema: public; Owner: citizen_user
--

ALTER TABLE ONLY rta_office_test_config
    ADD CONSTRAINT fk_qemv0tcj1wwnreplb19dt24aq FOREIGN KEY (rta_office_schedule_id) REFERENCES rta_office_schedule(rta_office_schedule_id);


--
-- Name: fk_qtgf606j8da38kmurmbwyshoi; Type: FK CONSTRAINT; Schema: public; Owner: citizen_user
--

ALTER TABLE ONLY license_holder_approved_details
    ADD CONSTRAINT fk_qtgf606j8da38kmurmbwyshoi FOREIGN KEY (application_id) REFERENCES application(application_id);


--
-- Name: fk_rde96tk23bwkjth73bms3tmgh; Type: FK CONSTRAINT; Schema: public; Owner: citizen_user
--

ALTER TABLE ONLY citizen_invoice
    ADD CONSTRAINT fk_rde96tk23bwkjth73bms3tmgh FOREIGN KEY (application_id) REFERENCES application(application_id);


--
-- Name: fk_scooixdv6r39ypgk4srl8b1da; Type: FK CONSTRAINT; Schema: public; Owner: citizen_user
--

ALTER TABLE ONLY application_approval_history
    ADD CONSTRAINT fk_scooixdv6r39ypgk4srl8b1da FOREIGN KEY (application_id) REFERENCES application(application_id);


--
-- Name: fk_soxl0s9kshcr43jqkmg86ij2b; Type: FK CONSTRAINT; Schema: public; Owner: citizen_user
--

ALTER TABLE ONLY application
    ADD CONSTRAINT fk_soxl0s9kshcr43jqkmg86ij2b FOREIGN KEY (session_id) REFERENCES user_sessions(session_id);


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- Name: age_group_ref; Type: ACL; Schema: public; Owner: citizen_user
--

REVOKE ALL ON TABLE age_group_ref FROM PUBLIC;
REVOKE ALL ON TABLE age_group_ref FROM citizen_user;
GRANT ALL ON TABLE age_group_ref TO citizen_user;
GRANT SELECT ON TABLE age_group_ref TO read_user;


--
-- Name: application; Type: ACL; Schema: public; Owner: citizen_user
--

REVOKE ALL ON TABLE application FROM PUBLIC;
REVOKE ALL ON TABLE application FROM citizen_user;
GRANT ALL ON TABLE application TO citizen_user;
GRANT SELECT ON TABLE application TO read_user;


--
-- Name: application_approval_history; Type: ACL; Schema: public; Owner: citizen_user
--

REVOKE ALL ON TABLE application_approval_history FROM PUBLIC;
REVOKE ALL ON TABLE application_approval_history FROM citizen_user;
GRANT ALL ON TABLE application_approval_history TO citizen_user;
GRANT SELECT ON TABLE application_approval_history TO read_user;


--
-- Name: application_attachments; Type: ACL; Schema: public; Owner: citizen_user
--

REVOKE ALL ON TABLE application_attachments FROM PUBLIC;
REVOKE ALL ON TABLE application_attachments FROM citizen_user;
GRANT ALL ON TABLE application_attachments TO citizen_user;
GRANT SELECT ON TABLE application_attachments TO read_user;


--
-- Name: application_bank_transaction_detail; Type: ACL; Schema: public; Owner: citizen_user
--

REVOKE ALL ON TABLE application_bank_transaction_detail FROM PUBLIC;
REVOKE ALL ON TABLE application_bank_transaction_detail FROM citizen_user;
GRANT ALL ON TABLE application_bank_transaction_detail TO citizen_user;
GRANT SELECT ON TABLE application_bank_transaction_detail TO read_user;


--
-- Name: application_fee_detail; Type: ACL; Schema: public; Owner: citizen_user
--

REVOKE ALL ON TABLE application_fee_detail FROM PUBLIC;
REVOKE ALL ON TABLE application_fee_detail FROM citizen_user;
GRANT ALL ON TABLE application_fee_detail TO citizen_user;
GRANT SELECT ON TABLE application_fee_detail TO read_user;


--
-- Name: application_form_data; Type: ACL; Schema: public; Owner: citizen_user
--

REVOKE ALL ON TABLE application_form_data FROM PUBLIC;
REVOKE ALL ON TABLE application_form_data FROM citizen_user;
GRANT ALL ON TABLE application_form_data TO citizen_user;
GRANT SELECT ON TABLE application_form_data TO read_user;


--
-- Name: application_tax_detail; Type: ACL; Schema: public; Owner: citizen_user
--

REVOKE ALL ON TABLE application_tax_detail FROM PUBLIC;
REVOKE ALL ON TABLE application_tax_detail FROM citizen_user;
GRANT ALL ON TABLE application_tax_detail TO citizen_user;
GRANT SELECT ON TABLE application_tax_detail TO read_user;


--
-- Name: citizen_address; Type: ACL; Schema: public; Owner: citizen_user
--

REVOKE ALL ON TABLE citizen_address FROM PUBLIC;
REVOKE ALL ON TABLE citizen_address FROM citizen_user;
GRANT ALL ON TABLE citizen_address TO citizen_user;
GRANT SELECT ON TABLE citizen_address TO read_user;


--
-- Name: citizen_address_outside_ap; Type: ACL; Schema: public; Owner: citizen_user
--

REVOKE ALL ON TABLE citizen_address_outside_ap FROM PUBLIC;
REVOKE ALL ON TABLE citizen_address_outside_ap FROM citizen_user;
GRANT ALL ON TABLE citizen_address_outside_ap TO citizen_user;
GRANT SELECT ON TABLE citizen_address_outside_ap TO read_user;


--
-- Name: citizen_info; Type: ACL; Schema: public; Owner: citizen_user
--

REVOKE ALL ON TABLE citizen_info FROM PUBLIC;
REVOKE ALL ON TABLE citizen_info FROM citizen_user;
GRANT ALL ON TABLE citizen_info TO citizen_user;
GRANT SELECT ON TABLE citizen_info TO read_user;


--
-- Name: citizen_invoice; Type: ACL; Schema: public; Owner: citizen_user
--

REVOKE ALL ON TABLE citizen_invoice FROM PUBLIC;
REVOKE ALL ON TABLE citizen_invoice FROM citizen_user;
GRANT ALL ON TABLE citizen_invoice TO citizen_user;
GRANT SELECT ON TABLE citizen_invoice TO read_user;


--
-- Name: dl_series_master; Type: ACL; Schema: public; Owner: citizen_user
--

REVOKE ALL ON TABLE dl_series_master FROM PUBLIC;
REVOKE ALL ON TABLE dl_series_master FROM citizen_user;
GRANT ALL ON TABLE dl_series_master TO citizen_user;
GRANT SELECT ON TABLE dl_series_master TO read_user;


--
-- Name: document_master; Type: ACL; Schema: public; Owner: citizen_user
--

REVOKE ALL ON TABLE document_master FROM PUBLIC;
REVOKE ALL ON TABLE document_master FROM citizen_user;
GRANT ALL ON TABLE document_master TO citizen_user;
GRANT SELECT ON TABLE document_master TO read_user;


--
-- Name: event; Type: ACL; Schema: public; Owner: citizen_user
--

REVOKE ALL ON TABLE event FROM PUBLIC;
REVOKE ALL ON TABLE event FROM citizen_user;
GRANT ALL ON TABLE event TO citizen_user;
GRANT SELECT ON TABLE event TO read_user;


--
-- Name: holidays; Type: ACL; Schema: public; Owner: citizen_user
--

REVOKE ALL ON TABLE holidays FROM PUBLIC;
REVOKE ALL ON TABLE holidays FROM citizen_user;
GRANT ALL ON TABLE holidays TO citizen_user;
GRANT SELECT ON TABLE holidays TO read_user;


--
-- Name: license_holder_approved_details; Type: ACL; Schema: public; Owner: citizen_user
--

REVOKE ALL ON TABLE license_holder_approved_details FROM PUBLIC;
REVOKE ALL ON TABLE license_holder_approved_details FROM citizen_user;
GRANT ALL ON TABLE license_holder_approved_details TO citizen_user;
GRANT SELECT ON TABLE license_holder_approved_details TO read_user;


--
-- Name: license_permit_details; Type: ACL; Schema: public; Owner: citizen_user
--

REVOKE ALL ON TABLE license_permit_details FROM PUBLIC;
REVOKE ALL ON TABLE license_permit_details FROM citizen_user;
GRANT ALL ON TABLE license_permit_details TO citizen_user;
GRANT SELECT ON TABLE license_permit_details TO read_user;


--
-- Name: license_permit_details_history; Type: ACL; Schema: public; Owner: citizen_user
--

REVOKE ALL ON TABLE license_permit_details_history FROM PUBLIC;
REVOKE ALL ON TABLE license_permit_details_history FROM citizen_user;
GRANT ALL ON TABLE license_permit_details_history TO citizen_user;
GRANT SELECT ON TABLE license_permit_details_history TO read_user;


--
-- Name: login_attempt; Type: ACL; Schema: public; Owner: citizen_user
--

REVOKE ALL ON TABLE login_attempt FROM PUBLIC;
REVOKE ALL ON TABLE login_attempt FROM citizen_user;
GRANT ALL ON TABLE login_attempt TO citizen_user;
GRANT SELECT ON TABLE login_attempt TO read_user;


--
-- Name: ot_token; Type: ACL; Schema: public; Owner: citizen_user
--

REVOKE ALL ON TABLE ot_token FROM PUBLIC;
REVOKE ALL ON TABLE ot_token FROM citizen_user;
GRANT ALL ON TABLE ot_token TO citizen_user;
GRANT SELECT ON TABLE ot_token TO read_user;


--
-- Name: pending_username; Type: ACL; Schema: public; Owner: citizen_user
--

REVOKE ALL ON TABLE pending_username FROM PUBLIC;
REVOKE ALL ON TABLE pending_username FROM citizen_user;
GRANT ALL ON TABLE pending_username TO citizen_user;
GRANT SELECT ON TABLE pending_username TO read_user;


--
-- Name: question_options; Type: ACL; Schema: public; Owner: citizen_user
--

REVOKE ALL ON TABLE question_options FROM PUBLIC;
REVOKE ALL ON TABLE question_options FROM citizen_user;
GRANT ALL ON TABLE question_options TO citizen_user;
GRANT SELECT ON TABLE question_options TO read_user;


--
-- Name: questionnaire_feedback; Type: ACL; Schema: public; Owner: citizen_user
--

REVOKE ALL ON TABLE questionnaire_feedback FROM PUBLIC;
REVOKE ALL ON TABLE questionnaire_feedback FROM citizen_user;
GRANT ALL ON TABLE questionnaire_feedback TO citizen_user;
GRANT SELECT ON TABLE questionnaire_feedback TO read_user;


--
-- Name: questions; Type: ACL; Schema: public; Owner: citizen_user
--

REVOKE ALL ON TABLE questions FROM PUBLIC;
REVOKE ALL ON TABLE questions FROM citizen_user;
GRANT ALL ON TABLE questions TO citizen_user;
GRANT SELECT ON TABLE questions TO read_user;


--
-- Name: rejected_app_removed_exe_id_hist; Type: ACL; Schema: public; Owner: citizen_user
--

REVOKE ALL ON TABLE rejected_app_removed_exe_id_hist FROM PUBLIC;
REVOKE ALL ON TABLE rejected_app_removed_exe_id_hist FROM citizen_user;
GRANT ALL ON TABLE rejected_app_removed_exe_id_hist TO citizen_user;
GRANT SELECT ON TABLE rejected_app_removed_exe_id_hist TO read_user;


--
-- Name: rta_office_ipaddress; Type: ACL; Schema: public; Owner: citizen_user
--

REVOKE ALL ON TABLE rta_office_ipaddress FROM PUBLIC;
REVOKE ALL ON TABLE rta_office_ipaddress FROM citizen_user;
GRANT ALL ON TABLE rta_office_ipaddress TO citizen_user;
GRANT SELECT ON TABLE rta_office_ipaddress TO read_user;


--
-- Name: rta_office_schedule; Type: ACL; Schema: public; Owner: citizen_user
--

REVOKE ALL ON TABLE rta_office_schedule FROM PUBLIC;
REVOKE ALL ON TABLE rta_office_schedule FROM citizen_user;
GRANT ALL ON TABLE rta_office_schedule TO citizen_user;
GRANT SELECT ON TABLE rta_office_schedule TO read_user;


--
-- Name: rta_office_test_config; Type: ACL; Schema: public; Owner: citizen_user
--

REVOKE ALL ON TABLE rta_office_test_config FROM PUBLIC;
REVOKE ALL ON TABLE rta_office_test_config FROM citizen_user;
GRANT ALL ON TABLE rta_office_test_config TO citizen_user;
GRANT SELECT ON TABLE rta_office_test_config TO read_user;


--
-- Name: service_master; Type: ACL; Schema: public; Owner: citizen_user
--

REVOKE ALL ON TABLE service_master FROM PUBLIC;
REVOKE ALL ON TABLE service_master FROM citizen_user;
GRANT ALL ON TABLE service_master TO citizen_user;
GRANT SELECT ON TABLE service_master TO read_user;


--
-- Name: slot_applications; Type: ACL; Schema: public; Owner: citizen_user
--

REVOKE ALL ON TABLE slot_applications FROM PUBLIC;
REVOKE ALL ON TABLE slot_applications FROM citizen_user;
GRANT ALL ON TABLE slot_applications TO citizen_user;
GRANT SELECT ON TABLE slot_applications TO read_user;


--
-- Name: slots; Type: ACL; Schema: public; Owner: citizen_user
--

REVOKE ALL ON TABLE slots FROM PUBLIC;
REVOKE ALL ON TABLE slots FROM citizen_user;
GRANT ALL ON TABLE slots TO citizen_user;
GRANT SELECT ON TABLE slots TO read_user;


--
-- Name: tax_type; Type: ACL; Schema: public; Owner: citizen_user
--

REVOKE ALL ON TABLE tax_type FROM PUBLIC;
REVOKE ALL ON TABLE tax_type FROM citizen_user;
GRANT ALL ON TABLE tax_type TO citizen_user;
GRANT SELECT ON TABLE tax_type TO read_user;


--
-- Name: transaction_history; Type: ACL; Schema: public; Owner: citizen_user
--

REVOKE ALL ON TABLE transaction_history FROM PUBLIC;
REVOKE ALL ON TABLE transaction_history FROM citizen_user;
GRANT ALL ON TABLE transaction_history TO citizen_user;
GRANT SELECT ON TABLE transaction_history TO read_user;


--
-- Name: user_sessions; Type: ACL; Schema: public; Owner: citizen_user
--

REVOKE ALL ON TABLE user_sessions FROM PUBLIC;
REVOKE ALL ON TABLE user_sessions FROM citizen_user;
GRANT ALL ON TABLE user_sessions TO citizen_user;
GRANT SELECT ON TABLE user_sessions TO read_user;


--
-- Name: vehicle_class_mst; Type: ACL; Schema: public; Owner: citizen_user
--

REVOKE ALL ON TABLE vehicle_class_mst FROM PUBLIC;
REVOKE ALL ON TABLE vehicle_class_mst FROM citizen_user;
GRANT ALL ON TABLE vehicle_class_mst TO citizen_user;
GRANT SELECT ON TABLE vehicle_class_mst TO read_user;


--
-- Name: vehicle_class_ref; Type: ACL; Schema: public; Owner: citizen_user
--

REVOKE ALL ON TABLE vehicle_class_ref FROM PUBLIC;
REVOKE ALL ON TABLE vehicle_class_ref FROM citizen_user;
GRANT ALL ON TABLE vehicle_class_ref TO citizen_user;
GRANT SELECT ON TABLE vehicle_class_ref TO read_user;


--
-- Name: vehicle_class_tests_mst; Type: ACL; Schema: public; Owner: citizen_user
--

REVOKE ALL ON TABLE vehicle_class_tests_mst FROM PUBLIC;
REVOKE ALL ON TABLE vehicle_class_tests_mst FROM citizen_user;
GRANT ALL ON TABLE vehicle_class_tests_mst TO citizen_user;
GRANT SELECT ON TABLE vehicle_class_tests_mst TO read_user;


--
-- PostgreSQL database dump complete
--

