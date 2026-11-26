-- Exorcity: schema + Bengaluru demo data for Supabase (generated with pg_dump from a clean local seed).
-- Includes Flyway history (V1), so the backend sees the DB as migrated and the seeder skips.
-- Run once in Supabase SQL Editor on an empty project.
BEGIN;
--
-- PostgreSQL database dump
--

\restrict 1hSG1euIu6gvYzHAMDZrgQtfgjyUe0IrDpO3OX4WPzwXGIPtfPej6gTktQ9uIuq

-- Dumped from database version 16.16
-- Dumped by pg_dump version 16.16

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET search_path = public;
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: public; Type: SCHEMA; Schema: -; Owner: -
--



SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: confirmations; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.confirmations (
    id bigint NOT NULL,
    spot_id bigint NOT NULL,
    user_id bigint NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: confirmations_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.confirmations_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: confirmations_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.confirmations_id_seq OWNED BY public.confirmations.id;


--
-- Name: flyway_schema_history; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.flyway_schema_history (
    installed_rank integer NOT NULL,
    version character varying(50),
    description character varying(200) NOT NULL,
    type character varying(20) NOT NULL,
    script character varying(1000) NOT NULL,
    checksum integer,
    installed_by character varying(100) NOT NULL,
    installed_on timestamp without time zone DEFAULT now() NOT NULL,
    execution_time integer NOT NULL,
    success boolean NOT NULL
);


--
-- Name: garbage_spots; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.garbage_spots (
    id bigint NOT NULL,
    type character varying(30) NOT NULL,
    description character varying(1000),
    latitude double precision NOT NULL,
    longitude double precision NOT NULL,
    before_photo_url character varying(1000) NOT NULL,
    after_photo_url character varying(1000),
    status character varying(20) DEFAULT 'OPEN'::character varying NOT NULL,
    reported_by bigint NOT NULL,
    cleaned_by bigint,
    linked_entity_id bigint,
    ward character varying(100),
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    cleaned_at timestamp with time zone,
    confirmation_count integer DEFAULT 0 NOT NULL,
    cursed boolean DEFAULT false NOT NULL,
    first_confirmation_rewarded boolean DEFAULT false NOT NULL,
    curse_penalty_applied boolean DEFAULT false NOT NULL,
    overdue_penalty_applied boolean DEFAULT false NOT NULL
);


--
-- Name: garbage_spots_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.garbage_spots_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: garbage_spots_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.garbage_spots_id_seq OWNED BY public.garbage_spots.id;


--
-- Name: responsible_entities; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.responsible_entities (
    id bigint NOT NULL,
    name character varying(150) NOT NULL,
    type character varying(30) NOT NULL,
    latitude double precision NOT NULL,
    longitude double precision NOT NULL,
    ward character varying(100) NOT NULL,
    civic_score integer DEFAULT 600 NOT NULL,
    CONSTRAINT responsible_entities_civic_score_check CHECK (((civic_score >= 0) AND (civic_score <= 900)))
);


--
-- Name: responsible_entities_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.responsible_entities_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: responsible_entities_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.responsible_entities_id_seq OWNED BY public.responsible_entities.id;


--
-- Name: score_events; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.score_events (
    id bigint NOT NULL,
    user_id bigint,
    entity_id bigint,
    spot_id bigint,
    points integer NOT NULL,
    reason character varying(50) NOT NULL,
    description character varying(255),
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT chk_score_event_owner CHECK (((user_id IS NULL) <> (entity_id IS NULL)))
);


--
-- Name: score_events_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.score_events_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: score_events_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.score_events_id_seq OWNED BY public.score_events.id;


--
-- Name: users; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.users (
    id bigint NOT NULL,
    name character varying(100) NOT NULL,
    email character varying(255) NOT NULL,
    password_hash character varying(100) NOT NULL,
    role character varying(30) NOT NULL,
    civic_score integer DEFAULT 600 NOT NULL,
    entity_id bigint,
    ward character varying(100),
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT users_civic_score_check CHECK (((civic_score >= 0) AND (civic_score <= 900)))
);


--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- Name: confirmations id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.confirmations ALTER COLUMN id SET DEFAULT nextval('public.confirmations_id_seq'::regclass);


--
-- Name: garbage_spots id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.garbage_spots ALTER COLUMN id SET DEFAULT nextval('public.garbage_spots_id_seq'::regclass);


--
-- Name: responsible_entities id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.responsible_entities ALTER COLUMN id SET DEFAULT nextval('public.responsible_entities_id_seq'::regclass);


--
-- Name: score_events id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.score_events ALTER COLUMN id SET DEFAULT nextval('public.score_events_id_seq'::regclass);


--
-- Name: users id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- Data for Name: confirmations; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.confirmations (id, spot_id, user_id, created_at) VALUES
	(1, 1, 2, '2026-11-14 13:09:49.237105+00'),
	(2, 1, 3, '2026-11-14 14:09:49.237105+00'),
	(3, 1, 4, '2026-11-14 15:09:49.237105+00'),
	(4, 1, 5, '2026-11-14 16:09:49.237105+00'),
	(5, 1, 6, '2026-11-14 17:09:49.237105+00'),
	(6, 1, 7, '2026-11-14 18:09:49.237105+00'),
	(7, 2, 3, '2026-11-17 12:09:49.237105+00'),
	(8, 2, 4, '2026-11-17 13:09:49.237105+00'),
	(9, 2, 5, '2026-11-17 14:09:49.237105+00'),
	(10, 3, 4, '2026-11-21 11:09:49.237105+00'),
	(11, 3, 5, '2026-11-21 12:09:49.237105+00'),
	(12, 4, 5, '2026-11-24 10:09:49.237105+00'),
	(13, 5, 6, '2026-10-23 09:09:49.237105+00'),
	(14, 5, 7, '2026-10-23 10:09:49.237105+00'),
	(15, 5, 8, '2026-10-23 11:09:49.237105+00'),
	(16, 5, 9, '2026-10-23 12:09:49.237105+00'),
	(17, 5, 10, '2026-10-23 13:09:49.237105+00'),
	(18, 5, 2, '2026-10-23 14:09:49.237105+00'),
	(19, 5, 3, '2026-10-23 15:09:49.237105+00'),
	(20, 5, 4, '2026-10-23 16:09:49.237105+00'),
	(21, 6, 7, '2026-11-08 13:09:49.237105+00'),
	(22, 6, 8, '2026-11-08 14:09:49.237105+00'),
	(23, 6, 9, '2026-11-08 15:09:49.237105+00'),
	(24, 6, 10, '2026-11-08 16:09:49.237105+00'),
	(25, 8, 9, '2026-11-20 11:09:49.237105+00'),
	(26, 8, 10, '2026-11-20 12:09:49.237105+00'),
	(27, 10, 2, '2026-11-22 09:09:49.237105+00'),
	(28, 10, 3, '2026-11-22 10:09:49.237105+00'),
	(29, 11, 2, '2026-11-16 13:09:49.237105+00'),
	(30, 11, 3, '2026-11-16 14:09:49.237105+00'),
	(31, 11, 4, '2026-11-16 15:09:49.237105+00'),
	(32, 11, 5, '2026-11-16 16:09:49.237105+00'),
	(33, 11, 6, '2026-11-16 17:09:49.237105+00'),
	(34, 12, 3, '2026-11-05 12:09:49.237105+00'),
	(35, 12, 4, '2026-11-05 13:09:49.237105+00'),
	(36, 12, 5, '2026-11-05 14:09:49.237105+00'),
	(37, 13, 4, '2026-10-12 11:09:49.237105+00'),
	(38, 13, 5, '2026-10-12 12:09:49.237105+00'),
	(39, 13, 6, '2026-10-12 13:09:49.237105+00'),
	(40, 13, 7, '2026-10-12 14:09:49.237105+00'),
	(41, 13, 8, '2026-10-12 15:09:49.237105+00'),
	(42, 13, 9, '2026-10-12 16:09:49.237105+00'),
	(43, 13, 2, '2026-10-12 17:09:49.237105+00'),
	(44, 14, 5, '2026-11-19 10:09:49.237105+00'),
	(45, 15, 6, '2026-11-24 09:09:49.237105+00'),
	(46, 17, 8, '2026-11-10 12:09:49.237105+00'),
	(47, 17, 9, '2026-11-10 13:09:49.237105+00'),
	(48, 17, 10, '2026-11-10 14:09:49.237105+00'),
	(49, 17, 2, '2026-11-10 15:09:49.237105+00'),
	(50, 17, 3, '2026-11-10 16:09:49.237105+00');
INSERT INTO public.confirmations (id, spot_id, user_id, created_at) VALUES
	(51, 17, 4, '2026-11-10 17:09:49.237105+00'),
	(52, 17, 5, '2026-11-10 18:09:49.237105+00'),
	(53, 18, 9, '2026-11-23 11:09:49.237105+00'),
	(54, 18, 10, '2026-11-23 12:09:49.237105+00'),
	(55, 19, 10, '2026-10-31 10:09:49.237105+00'),
	(56, 19, 2, '2026-10-31 11:09:49.237105+00'),
	(57, 19, 3, '2026-10-31 12:09:49.237105+00'),
	(58, 19, 4, '2026-10-31 13:09:49.237105+00'),
	(59, 20, 2, '2026-10-17 09:09:49.237105+00'),
	(60, 20, 3, '2026-10-17 10:09:49.237105+00'),
	(61, 20, 4, '2026-10-17 11:09:49.237105+00'),
	(62, 20, 5, '2026-10-17 12:09:49.237105+00'),
	(63, 20, 6, '2026-10-17 13:09:49.237105+00'),
	(64, 20, 7, '2026-10-17 14:09:49.237105+00'),
	(65, 20, 8, '2026-10-17 15:09:49.237105+00'),
	(66, 22, 3, '2026-11-16 12:09:49.237105+00'),
	(67, 22, 4, '2026-11-16 13:09:49.237105+00'),
	(68, 23, 4, '2026-11-18 11:09:49.237105+00'),
	(69, 23, 5, '2026-11-18 12:09:49.237105+00'),
	(70, 23, 6, '2026-11-18 13:09:49.237105+00'),
	(71, 24, 5, '2026-11-12 10:09:49.237105+00'),
	(72, 24, 6, '2026-11-12 11:09:49.237105+00'),
	(73, 26, 7, '2026-10-26 13:09:49.237105+00'),
	(74, 26, 8, '2026-10-26 14:09:49.237105+00'),
	(75, 26, 9, '2026-10-26 15:09:49.237105+00'),
	(76, 26, 10, '2026-10-26 16:09:49.237105+00'),
	(77, 26, 2, '2026-10-26 17:09:49.237105+00'),
	(78, 26, 3, '2026-10-26 18:09:49.237105+00'),
	(79, 27, 8, '2026-11-06 12:09:49.237105+00'),
	(80, 27, 9, '2026-11-06 13:09:49.237105+00'),
	(81, 27, 10, '2026-11-06 14:09:49.237105+00'),
	(82, 28, 9, '2026-11-25 11:09:49.237105+00'),
	(83, 29, 10, '2026-11-04 10:09:49.237105+00'),
	(84, 29, 2, '2026-11-04 11:09:49.237105+00'),
	(85, 29, 3, '2026-11-04 12:09:49.237105+00'),
	(86, 29, 4, '2026-11-04 13:09:49.237105+00'),
	(87, 29, 5, '2026-11-04 14:09:49.237105+00'),
	(88, 29, 6, '2026-11-04 15:09:49.237105+00'),
	(89, 29, 7, '2026-11-04 16:09:49.237105+00'),
	(90, 30, 2, '2026-09-27 09:09:49.237105+00'),
	(91, 30, 3, '2026-09-27 10:09:49.237105+00'),
	(92, 30, 4, '2026-09-27 11:09:49.237105+00'),
	(93, 30, 5, '2026-09-27 12:09:49.237105+00'),
	(94, 30, 6, '2026-09-27 13:09:49.237105+00'),
	(95, 30, 7, '2026-09-27 14:09:49.237105+00'),
	(96, 30, 8, '2026-09-27 15:09:49.237105+00'),
	(97, 31, 2, '2026-11-20 13:09:49.237105+00'),
	(98, 31, 3, '2026-11-20 14:09:49.237105+00'),
	(99, 32, 3, '2026-11-15 12:09:49.237105+00'),
	(100, 34, 5, '2026-11-22 10:09:49.237105+00');
INSERT INTO public.confirmations (id, spot_id, user_id, created_at) VALUES
	(101, 35, 6, '2026-11-21 09:09:49.237105+00'),
	(102, 35, 7, '2026-11-21 10:09:49.237105+00'),
	(103, 35, 8, '2026-11-21 11:09:49.237105+00'),
	(104, 36, 7, '2026-11-13 13:09:49.237105+00'),
	(105, 36, 8, '2026-11-13 14:09:49.237105+00'),
	(106, 36, 9, '2026-11-13 15:09:49.237105+00'),
	(107, 36, 10, '2026-11-13 16:09:49.237105+00'),
	(108, 36, 2, '2026-11-13 17:09:49.237105+00'),
	(109, 37, 8, '2026-11-07 12:09:49.237105+00'),
	(110, 37, 9, '2026-11-07 13:09:49.237105+00'),
	(111, 38, 9, '2026-10-24 11:09:49.237105+00'),
	(112, 38, 10, '2026-10-24 12:09:49.237105+00'),
	(113, 38, 2, '2026-10-24 13:09:49.237105+00'),
	(114, 38, 3, '2026-10-24 14:09:49.237105+00'),
	(115, 38, 4, '2026-10-24 15:09:49.237105+00'),
	(116, 38, 5, '2026-10-24 16:09:49.237105+00'),
	(117, 38, 6, '2026-10-24 17:09:49.237105+00'),
	(118, 40, 2, '2026-11-17 09:09:49.237105+00'),
	(119, 40, 3, '2026-11-17 10:09:49.237105+00');


--
-- Data for Name: flyway_schema_history; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES
	(1, '1', 'init', 'SQL', 'V1__init.sql', 860452220, 'exorcity', '2026-11-26 17:39:33.092221', 64, true);


--
-- Data for Name: garbage_spots; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.garbage_spots (id, type, description, latitude, longitude, before_photo_url, after_photo_url, status, reported_by, cleaned_by, linked_entity_id, ward, created_at, cleaned_at, confirmation_count, cursed, first_confirmation_rewarded, curse_penalty_applied, overdue_penalty_applied) VALUES
	(7, 'OVERFLOWING_BIN', 'Bin outside the market not emptied today', 12.938, 77.618, '/seed/overflowing-bin.svg', NULL, 'OPEN', 7, NULL, NULL, 'Koramangala', '2026-11-25 10:09:49.237105+00', NULL, 0, false, false, false, false),
	(16, 'OVERFLOWING_BIN', 'Bin overflowing after the weekend', 12.965, 77.645, '/seed/overflowing-bin.svg', NULL, 'OPEN', 5, NULL, NULL, 'Indiranagar', '2026-11-26 11:09:49.237105+00', NULL, 0, false, false, false, false),
	(21, 'ROADSIDE_PILE', 'Fresh pile of bags near the metro station exit', 12.975, 77.742, '/seed/roadside-pile.svg', NULL, 'OPEN', 10, NULL, NULL, 'Whitefield', '2026-11-26 11:09:49.237105+00', NULL, 0, false, false, false, false),
	(25, 'PLASTIC_WASTE', 'Plastic sachets scattered near the park', 12.92, 77.598, '/seed/plastic-waste.svg', NULL, 'OPEN', 1, NULL, NULL, 'Jayanagar', '2026-11-24 07:09:49.237105+00', NULL, 0, false, false, false, false),
	(33, 'ROADSIDE_PILE', 'Garbage bags dumped at the junction', 13.033, 77.594, '/seed/roadside-pile.svg', NULL, 'OPEN', 3, NULL, NULL, 'Hebbal', '2026-11-23 09:09:49.237105+00', NULL, 0, false, false, false, false),
	(39, 'LAKE_OR_DRAIN', 'Rainwater drain blocked with waste', 13.01, 77.578, '/seed/lake-or-drain.svg', NULL, 'OPEN', 9, NULL, NULL, 'Malleshwaram', '2026-11-25 08:09:49.237105+00', NULL, 0, false, false, false, false),
	(1, 'ILLEGAL_DUMP', 'Construction debris and household waste dumped behind the society wall', 12.936, 77.625, '/seed/illegal-dump.svg', NULL, 'OPEN', 1, NULL, 1, 'Koramangala', '2026-11-14 11:09:49.237105+00', NULL, 6, true, true, true, true),
	(2, 'ROADSIDE_PILE', 'Growing pile of mixed waste on the footpath next to the wall', 12.93627, 77.625, '/seed/roadside-pile.svg', NULL, 'OPEN', 2, NULL, 1, 'Koramangala', '2026-11-17 10:09:49.237105+00', NULL, 3, true, true, true, true),
	(3, 'PLASTIC_WASTE', 'Plastic bags and bottles blowing across the lane', 12.936, 77.62528, '/seed/plastic-waste.svg', NULL, 'OPEN', 3, NULL, 1, 'Koramangala', '2026-11-21 09:09:49.237105+00', NULL, 2, true, true, true, false),
	(4, 'OVERFLOWING_BIN', 'Community bin overflowing onto the road', 12.93627, 77.62528, '/seed/overflowing-bin.svg', NULL, 'OPEN', 4, NULL, 1, 'Koramangala', '2026-11-24 08:09:49.237105+00', NULL, 1, true, true, true, false),
	(5, 'ROADSIDE_PILE', 'Old garbage heap near the bus stop that nobody owns', 12.9325, 77.62, '/seed/roadside-pile.svg', NULL, 'OPEN', 5, NULL, NULL, 'Koramangala', '2026-10-23 07:09:49.237105+00', NULL, 8, false, true, false, false),
	(6, 'LAKE_OR_DRAIN', 'Storm drain choked with plastic and food waste', 12.93, 77.629, '/seed/lake-or-drain.svg', NULL, 'OPEN', 6, NULL, NULL, 'Koramangala', '2026-11-08 11:09:49.237105+00', NULL, 4, false, true, false, false),
	(8, 'PLASTIC_WASTE', 'Snack wrappers piled near the society gate', 12.934, 77.623, '/seed/plastic-waste.svg', '/seed/exorcised.svg', 'CLEANED', 1, 15, NULL, 'Koramangala', '2026-11-20 09:09:49.237105+00', '2026-11-25 12:09:49.237105+00', 2, false, true, false, false),
	(9, 'PLASTIC_WASTE', 'Test report with an unrelated photo', 12.94, 77.63, '/seed/plastic-waste.svg', NULL, 'REJECTED', 4, NULL, NULL, 'Koramangala', '2026-11-23 08:09:49.237105+00', NULL, 0, false, false, false, false),
	(10, 'OVERFLOWING_BIN', 'Bakery bins overflowing with packaging', 12.9722, 77.6418, '/seed/overflowing-bin.svg', NULL, 'OPEN', 1, NULL, 8, 'Indiranagar', '2026-11-22 07:09:49.237105+00', NULL, 2, false, true, false, false),
	(11, 'PLASTIC_WASTE', 'Takeaway cups and plastic near the campus back gate', 12.979, 77.64, '/seed/plastic-waste.svg', NULL, 'OPEN', 8, NULL, 5, 'Indiranagar', '2026-11-16 11:09:49.237105+00', NULL, 5, false, true, false, true),
	(12, 'ROADSIDE_PILE', 'Garbage bags left on the road median', 12.968, 77.638, '/seed/roadside-pile.svg', NULL, 'OPEN', 9, NULL, NULL, 'Indiranagar', '2026-11-05 10:09:49.237105+00', NULL, 3, false, true, false, false),
	(13, 'ILLEGAL_DUMP', 'Old furniture and e-waste dumped on an empty plot', 12.975, 77.646, '/seed/illegal-dump.svg', NULL, 'OPEN', 10, NULL, NULL, 'Indiranagar', '2026-10-12 09:09:49.237105+00', NULL, 7, false, true, false, false),
	(14, 'LAKE_OR_DRAIN', 'Open drain clogged with waste', 12.97, 77.635, '/seed/lake-or-drain.svg', NULL, 'OPEN', 2, NULL, NULL, 'Indiranagar', '2026-11-19 08:09:49.237105+00', NULL, 1, false, true, false, false),
	(15, 'ROADSIDE_PILE', 'Litter near the campus entrance', 12.9776, 77.6414, '/seed/roadside-pile.svg', '/seed/exorcised.svg', 'CLEANED', 3, 4, 5, 'Indiranagar', '2026-11-24 07:09:49.237105+00', '2026-11-26 12:09:49.237105+00', 1, false, true, false, false),
	(17, 'ILLEGAL_DUMP', 'Food court waste dumped outside the tech park fence', 12.9705, 77.751, '/seed/illegal-dump.svg', NULL, 'OPEN', 6, NULL, 4, 'Whitefield', '2026-11-10 10:09:49.237105+00', NULL, 7, false, true, false, true),
	(18, 'OVERFLOWING_BIN', 'Bins at the cab pickup point overflowing', 12.969, 77.749, '/seed/overflowing-bin.svg', NULL, 'OPEN', 7, NULL, 4, 'Whitefield', '2026-11-23 09:09:49.237105+00', NULL, 2, false, true, false, false),
	(19, 'PLASTIC_WASTE', 'Plastic waste along the service road', 12.965, 77.755, '/seed/plastic-waste.svg', NULL, 'OPEN', 8, NULL, NULL, 'Whitefield', '2026-10-31 08:09:49.237105+00', NULL, 4, false, true, false, false),
	(20, 'LAKE_OR_DRAIN', 'Lake edge covered in floating trash', 12.96, 77.745, '/seed/lake-or-drain.svg', NULL, 'OPEN', 9, NULL, NULL, 'Whitefield', '2026-10-17 07:09:49.237105+00', NULL, 7, false, true, false, false),
	(22, 'ROADSIDE_PILE', 'Garbage near the tech park gate', 12.97, 77.7495, '/seed/roadside-pile.svg', '/seed/exorcised.svg', 'CLEANED', 2, 12, 4, 'Whitefield', '2026-11-16 10:09:49.237105+00', '2026-11-24 12:09:49.237105+00', 2, false, true, false, false),
	(23, 'OVERFLOWING_BIN', 'Apartment bin spilling onto the footpath', 12.9255, 77.5945, '/seed/overflowing-bin.svg', NULL, 'OPEN', 3, NULL, 3, 'Jayanagar', '2026-11-18 09:09:49.237105+00', NULL, 3, false, true, false, true),
	(24, 'ROADSIDE_PILE', 'Mixed waste near the shopping complex', 12.93, 77.585, '/seed/roadside-pile.svg', NULL, 'OPEN', 4, NULL, NULL, 'Jayanagar', '2026-11-12 08:09:49.237105+00', NULL, 2, false, true, false, false),
	(26, 'LAKE_OR_DRAIN', 'Drain beside the lake road overflowing with garbage', 12.918, 77.59, '/seed/lake-or-drain.svg', NULL, 'OPEN', 5, NULL, NULL, 'Jayanagar', '2026-10-26 11:09:49.237105+00', NULL, 6, false, true, false, false),
	(27, 'ILLEGAL_DUMP', 'Debris dumped near the temple street', 12.923, 77.588, '/seed/illegal-dump.svg', '/seed/exorcised.svg', 'CLEANED', 6, 7, NULL, 'Jayanagar', '2026-11-06 10:09:49.237105+00', '2026-11-23 12:09:49.237105+00', 3, false, true, false, false),
	(28, 'ROADSIDE_PILE', 'Bags outside the apartment gate', 12.9245, 77.593, '/seed/roadside-pile.svg', '/seed/exorcised.svg', 'CLEANED', 7, 8, 3, 'Jayanagar', '2026-11-25 09:09:49.237105+00', '2026-11-26 12:09:49.237105+00', 1, false, true, false, false),
	(29, 'LAKE_OR_DRAIN', 'Lake shore littered with plastic near the residency', 13.0365, 77.598, '/seed/lake-or-drain.svg', NULL, 'OPEN', 8, NULL, 2, 'Hebbal', '2026-11-04 08:09:49.237105+00', NULL, 7, false, true, false, true),
	(30, 'ILLEGAL_DUMP', 'Huge dump under the flyover', 13.03, 77.6, '/seed/illegal-dump.svg', NULL, 'OPEN', 9, NULL, NULL, 'Hebbal', '2026-09-27 07:09:49.237105+00', NULL, 7, false, true, false, false),
	(31, 'PLASTIC_WASTE', 'Plastic packaging behind the campus cafeteria', 13.0455, 77.5928, '/seed/plastic-waste.svg', NULL, 'OPEN', 10, NULL, 6, 'Hebbal', '2026-11-20 11:09:49.237105+00', NULL, 2, false, true, false, false),
	(32, 'OVERFLOWING_BIN', 'Street bin overflowing for over a week', 13.04, 77.59, '/seed/overflowing-bin.svg', NULL, 'OPEN', 2, NULL, NULL, 'Hebbal', '2026-11-15 10:09:49.237105+00', NULL, 1, false, true, false, false),
	(34, 'LAKE_OR_DRAIN', 'Drain near the campus cleared', 13.044, 77.591, '/seed/lake-or-drain.svg', '/seed/exorcised.svg', 'CLEANED', 4, 5, 6, 'Hebbal', '2026-11-22 08:09:49.237105+00', '2026-11-25 12:09:49.237105+00', 1, false, true, false, false),
	(35, 'PLASTIC_WASTE', 'Plastic bags piling up beside the kirana store', 13.004, 77.5715, '/seed/plastic-waste.svg', NULL, 'OPEN', 5, NULL, 7, 'Malleshwaram', '2026-11-21 07:09:49.237105+00', NULL, 3, false, true, false, false),
	(36, 'OVERFLOWING_BIN', 'Shop-front bin overflowing', 13.003, 77.5702, '/seed/overflowing-bin.svg', NULL, 'OPEN', 6, NULL, 7, 'Malleshwaram', '2026-11-13 11:09:49.237105+00', NULL, 5, false, true, false, true),
	(37, 'ROADSIDE_PILE', 'Waste heap near the market cross road', 13.008, 77.565, '/seed/roadside-pile.svg', NULL, 'OPEN', 7, NULL, NULL, 'Malleshwaram', '2026-11-07 10:09:49.237105+00', NULL, 2, false, true, false, false),
	(38, 'ILLEGAL_DUMP', 'Construction waste blocking the footpath', 12.998, 77.575, '/seed/illegal-dump.svg', NULL, 'OPEN', 8, NULL, NULL, 'Malleshwaram', '2026-10-24 09:09:49.237105+00', NULL, 7, false, true, false, false),
	(40, 'ROADSIDE_PILE', 'Garbage near the vegetable market', 13.002, 77.569, '/seed/roadside-pile.svg', '/seed/exorcised.svg', 'CLEANED', 10, 1, NULL, 'Malleshwaram', '2026-11-17 07:09:49.237105+00', '2026-11-22 12:09:49.237105+00', 2, false, true, false, false),
	(41, 'PLASTIC_WASTE', 'Plastic dumped near the park gate', 12.982, 77.632, '/seed/plastic-waste.svg', '/seed/exorcised.svg', 'CLEANED', 5, 2, NULL, 'Indiranagar', '2026-11-01 11:09:49.237105+00', '2026-11-06 12:09:49.237105+00', 0, false, false, false, false),
	(42, 'ROADSIDE_PILE', 'Bags left at the bus shelter', 12.916, 77.586, '/seed/roadside-pile.svg', '/seed/exorcised.svg', 'CLEANED', 7, 2, NULL, 'Jayanagar', '2026-11-08 10:09:49.237105+00', '2026-11-11 12:09:49.237105+00', 0, false, false, false, false),
	(43, 'OVERFLOWING_BIN', 'Overflowing bin at the lake entrance', 13.038, 77.604, '/seed/overflowing-bin.svg', '/seed/exorcised.svg', 'CLEANED', 9, 2, NULL, 'Hebbal', '2026-11-14 09:09:49.237105+00', '2026-11-17 12:09:49.237105+00', 0, false, false, false, false);


--
-- Data for Name: responsible_entities; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.responsible_entities (id, name, type, latitude, longitude, ward, civic_score) VALUES
	(6, 'Nimbus Tech Campus', 'CORPORATE_CAMPUS', 13.045, 77.592, 'Hebbal', 600),
	(8, 'Fresh Bites Bakery', 'SHOP', 12.9719, 77.6412, 'Indiranagar', 600),
	(1, 'Green Meadows Society', 'SOCIETY', 12.9352, 77.6245, 'Koramangala', 370),
	(2, 'Lakeview Residency', 'SOCIETY', 13.0358, 77.597, 'Hebbal', 585),
	(3, 'Silver Oak Apartments', 'SOCIETY', 12.925, 77.5938, 'Jayanagar', 605),
	(4, 'Tech Park Alpha', 'CORPORATE_CAMPUS', 12.9698, 77.75, 'Whitefield', 585),
	(5, 'Orion Business Campus', 'CORPORATE_CAMPUS', 12.9784, 77.6408, 'Indiranagar', 605),
	(7, 'Corner Kirana Store', 'SHOP', 13.0035, 77.571, 'Malleshwaram', 585);


--
-- Data for Name: score_events; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.score_events (id, user_id, entity_id, spot_id, points, reason, description, created_at) VALUES
	(1, 2, NULL, 1, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-14 13:09:49.237105+00'),
	(2, 1, NULL, 1, 10, 'REPORT_FIRST_CONFIRMED', 'Your report got its first confirmation', '2026-11-14 13:09:49.237105+00'),
	(3, 3, NULL, 1, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-14 14:09:49.237105+00'),
	(4, 4, NULL, 1, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-14 15:09:49.237105+00'),
	(5, 5, NULL, 1, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-14 16:09:49.237105+00'),
	(6, 6, NULL, 1, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-14 17:09:49.237105+00'),
	(7, 7, NULL, 1, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-14 18:09:49.237105+00'),
	(8, 3, NULL, 2, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-17 12:09:49.237105+00'),
	(9, 2, NULL, 2, 10, 'REPORT_FIRST_CONFIRMED', 'Your report got its first confirmation', '2026-11-17 12:09:49.237105+00'),
	(10, 4, NULL, 2, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-17 13:09:49.237105+00'),
	(11, 5, NULL, 2, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-17 14:09:49.237105+00'),
	(12, 4, NULL, 3, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-21 11:09:49.237105+00'),
	(13, 3, NULL, 3, 10, 'REPORT_FIRST_CONFIRMED', 'Your report got its first confirmation', '2026-11-21 11:09:49.237105+00'),
	(14, 5, NULL, 3, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-21 12:09:49.237105+00'),
	(15, 5, NULL, 4, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-24 10:09:49.237105+00'),
	(16, 4, NULL, 4, 10, 'REPORT_FIRST_CONFIRMED', 'Your report got its first confirmation', '2026-11-24 10:09:49.237105+00'),
	(17, 6, NULL, 5, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-10-23 09:09:49.237105+00'),
	(18, 5, NULL, 5, 10, 'REPORT_FIRST_CONFIRMED', 'Your report got its first confirmation', '2026-10-23 09:09:49.237105+00'),
	(19, 7, NULL, 5, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-10-23 10:09:49.237105+00'),
	(20, 8, NULL, 5, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-10-23 11:09:49.237105+00'),
	(21, 9, NULL, 5, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-10-23 12:09:49.237105+00'),
	(22, 10, NULL, 5, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-10-23 13:09:49.237105+00'),
	(23, 2, NULL, 5, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-10-23 14:09:49.237105+00'),
	(24, 3, NULL, 5, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-10-23 15:09:49.237105+00'),
	(25, 4, NULL, 5, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-10-23 16:09:49.237105+00'),
	(26, 7, NULL, 6, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-08 13:09:49.237105+00'),
	(27, 6, NULL, 6, 10, 'REPORT_FIRST_CONFIRMED', 'Your report got its first confirmation', '2026-11-08 13:09:49.237105+00'),
	(28, 8, NULL, 6, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-08 14:09:49.237105+00'),
	(29, 9, NULL, 6, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-08 15:09:49.237105+00'),
	(30, 10, NULL, 6, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-08 16:09:49.237105+00'),
	(31, 9, NULL, 8, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-20 11:09:49.237105+00'),
	(32, 1, NULL, 8, 10, 'REPORT_FIRST_CONFIRMED', 'Your report got its first confirmation', '2026-11-20 11:09:49.237105+00'),
	(33, 10, NULL, 8, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-20 12:09:49.237105+00'),
	(34, 15, NULL, 8, 25, 'CLEANED_SPOT', 'Exorcised a garbage spot', '2026-11-25 12:09:49.237105+00'),
	(35, 4, NULL, 9, -30, 'REPORT_REJECTED', 'Report rejected as fake by a ward officer', '2026-11-24 08:09:49.237105+00'),
	(36, 2, NULL, 10, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-22 09:09:49.237105+00'),
	(37, 1, NULL, 10, 10, 'REPORT_FIRST_CONFIRMED', 'Your report got its first confirmation', '2026-11-22 09:09:49.237105+00'),
	(38, 3, NULL, 10, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-22 10:09:49.237105+00'),
	(39, 2, NULL, 11, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-16 13:09:49.237105+00'),
	(40, 8, NULL, 11, 10, 'REPORT_FIRST_CONFIRMED', 'Your report got its first confirmation', '2026-11-16 13:09:49.237105+00'),
	(41, 3, NULL, 11, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-16 14:09:49.237105+00'),
	(42, 4, NULL, 11, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-16 15:09:49.237105+00'),
	(43, 5, NULL, 11, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-16 16:09:49.237105+00'),
	(44, 6, NULL, 11, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-16 17:09:49.237105+00'),
	(45, 3, NULL, 12, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-05 12:09:49.237105+00'),
	(46, 9, NULL, 12, 10, 'REPORT_FIRST_CONFIRMED', 'Your report got its first confirmation', '2026-11-05 12:09:49.237105+00'),
	(47, 4, NULL, 12, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-05 13:09:49.237105+00'),
	(48, 5, NULL, 12, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-05 14:09:49.237105+00'),
	(49, 4, NULL, 13, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-10-12 11:09:49.237105+00'),
	(50, 10, NULL, 13, 10, 'REPORT_FIRST_CONFIRMED', 'Your report got its first confirmation', '2026-10-12 11:09:49.237105+00');
INSERT INTO public.score_events (id, user_id, entity_id, spot_id, points, reason, description, created_at) VALUES
	(51, 5, NULL, 13, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-10-12 12:09:49.237105+00'),
	(52, 6, NULL, 13, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-10-12 13:09:49.237105+00'),
	(53, 7, NULL, 13, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-10-12 14:09:49.237105+00'),
	(54, 8, NULL, 13, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-10-12 15:09:49.237105+00'),
	(55, 9, NULL, 13, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-10-12 16:09:49.237105+00'),
	(56, 2, NULL, 13, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-10-12 17:09:49.237105+00'),
	(57, 5, NULL, 14, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-19 10:09:49.237105+00'),
	(58, 2, NULL, 14, 10, 'REPORT_FIRST_CONFIRMED', 'Your report got its first confirmation', '2026-11-19 10:09:49.237105+00'),
	(59, 6, NULL, 15, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-24 09:09:49.237105+00'),
	(60, 3, NULL, 15, 10, 'REPORT_FIRST_CONFIRMED', 'Your report got its first confirmation', '2026-11-24 09:09:49.237105+00'),
	(61, 4, NULL, 15, 25, 'CLEANED_SPOT', 'Exorcised a garbage spot', '2026-11-26 12:09:49.237105+00'),
	(62, NULL, 5, 15, 20, 'LINKED_SPOT_CLEANED_FAST', 'Linked spot cleaned within 3 days', '2026-11-26 12:09:49.237105+00'),
	(63, 8, NULL, 17, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-10 12:09:49.237105+00'),
	(64, 6, NULL, 17, 10, 'REPORT_FIRST_CONFIRMED', 'Your report got its first confirmation', '2026-11-10 12:09:49.237105+00'),
	(65, 9, NULL, 17, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-10 13:09:49.237105+00'),
	(66, 10, NULL, 17, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-10 14:09:49.237105+00'),
	(67, 2, NULL, 17, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-10 15:09:49.237105+00'),
	(68, 3, NULL, 17, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-10 16:09:49.237105+00'),
	(69, 4, NULL, 17, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-10 17:09:49.237105+00'),
	(70, 5, NULL, 17, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-10 18:09:49.237105+00'),
	(71, 9, NULL, 18, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-23 11:09:49.237105+00'),
	(72, 7, NULL, 18, 10, 'REPORT_FIRST_CONFIRMED', 'Your report got its first confirmation', '2026-11-23 11:09:49.237105+00'),
	(73, 10, NULL, 18, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-23 12:09:49.237105+00'),
	(74, 10, NULL, 19, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-10-31 10:09:49.237105+00'),
	(75, 8, NULL, 19, 10, 'REPORT_FIRST_CONFIRMED', 'Your report got its first confirmation', '2026-10-31 10:09:49.237105+00'),
	(76, 2, NULL, 19, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-10-31 11:09:49.237105+00'),
	(77, 3, NULL, 19, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-10-31 12:09:49.237105+00'),
	(78, 4, NULL, 19, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-10-31 13:09:49.237105+00'),
	(79, 2, NULL, 20, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-10-17 09:09:49.237105+00'),
	(80, 9, NULL, 20, 10, 'REPORT_FIRST_CONFIRMED', 'Your report got its first confirmation', '2026-10-17 09:09:49.237105+00'),
	(81, 3, NULL, 20, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-10-17 10:09:49.237105+00'),
	(82, 4, NULL, 20, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-10-17 11:09:49.237105+00'),
	(83, 5, NULL, 20, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-10-17 12:09:49.237105+00'),
	(84, 6, NULL, 20, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-10-17 13:09:49.237105+00'),
	(85, 7, NULL, 20, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-10-17 14:09:49.237105+00'),
	(86, 8, NULL, 20, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-10-17 15:09:49.237105+00'),
	(87, 3, NULL, 22, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-16 12:09:49.237105+00'),
	(88, 2, NULL, 22, 10, 'REPORT_FIRST_CONFIRMED', 'Your report got its first confirmation', '2026-11-16 12:09:49.237105+00'),
	(89, 4, NULL, 22, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-16 13:09:49.237105+00'),
	(90, 12, NULL, 22, 25, 'CLEANED_SPOT', 'Exorcised a garbage spot', '2026-11-24 12:09:49.237105+00'),
	(91, 4, NULL, 23, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-18 11:09:49.237105+00'),
	(92, 3, NULL, 23, 10, 'REPORT_FIRST_CONFIRMED', 'Your report got its first confirmation', '2026-11-18 11:09:49.237105+00'),
	(93, 5, NULL, 23, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-18 12:09:49.237105+00'),
	(94, 6, NULL, 23, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-18 13:09:49.237105+00'),
	(95, 5, NULL, 24, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-12 10:09:49.237105+00'),
	(96, 4, NULL, 24, 10, 'REPORT_FIRST_CONFIRMED', 'Your report got its first confirmation', '2026-11-12 10:09:49.237105+00'),
	(97, 6, NULL, 24, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-12 11:09:49.237105+00'),
	(98, 7, NULL, 26, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-10-26 13:09:49.237105+00'),
	(99, 5, NULL, 26, 10, 'REPORT_FIRST_CONFIRMED', 'Your report got its first confirmation', '2026-10-26 13:09:49.237105+00'),
	(100, 8, NULL, 26, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-10-26 14:09:49.237105+00');
INSERT INTO public.score_events (id, user_id, entity_id, spot_id, points, reason, description, created_at) VALUES
	(101, 9, NULL, 26, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-10-26 15:09:49.237105+00'),
	(102, 10, NULL, 26, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-10-26 16:09:49.237105+00'),
	(103, 2, NULL, 26, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-10-26 17:09:49.237105+00'),
	(104, 3, NULL, 26, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-10-26 18:09:49.237105+00'),
	(105, 8, NULL, 27, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-06 12:09:49.237105+00'),
	(106, 6, NULL, 27, 10, 'REPORT_FIRST_CONFIRMED', 'Your report got its first confirmation', '2026-11-06 12:09:49.237105+00'),
	(107, 9, NULL, 27, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-06 13:09:49.237105+00'),
	(108, 10, NULL, 27, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-06 14:09:49.237105+00'),
	(109, 7, NULL, 27, 25, 'CLEANED_SPOT', 'Exorcised a garbage spot', '2026-11-23 12:09:49.237105+00'),
	(110, 9, NULL, 28, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-25 11:09:49.237105+00'),
	(111, 7, NULL, 28, 10, 'REPORT_FIRST_CONFIRMED', 'Your report got its first confirmation', '2026-11-25 11:09:49.237105+00'),
	(112, 8, NULL, 28, 25, 'CLEANED_SPOT', 'Exorcised a garbage spot', '2026-11-26 12:09:49.237105+00'),
	(113, NULL, 3, 28, 20, 'LINKED_SPOT_CLEANED_FAST', 'Linked spot cleaned within 3 days', '2026-11-26 12:09:49.237105+00'),
	(114, 10, NULL, 29, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-04 10:09:49.237105+00'),
	(115, 8, NULL, 29, 10, 'REPORT_FIRST_CONFIRMED', 'Your report got its first confirmation', '2026-11-04 10:09:49.237105+00'),
	(116, 2, NULL, 29, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-04 11:09:49.237105+00'),
	(117, 3, NULL, 29, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-04 12:09:49.237105+00'),
	(118, 4, NULL, 29, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-04 13:09:49.237105+00'),
	(119, 5, NULL, 29, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-04 14:09:49.237105+00'),
	(120, 6, NULL, 29, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-04 15:09:49.237105+00'),
	(121, 7, NULL, 29, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-04 16:09:49.237105+00'),
	(122, 2, NULL, 30, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-09-27 09:09:49.237105+00'),
	(123, 9, NULL, 30, 10, 'REPORT_FIRST_CONFIRMED', 'Your report got its first confirmation', '2026-09-27 09:09:49.237105+00'),
	(124, 3, NULL, 30, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-09-27 10:09:49.237105+00'),
	(125, 4, NULL, 30, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-09-27 11:09:49.237105+00'),
	(126, 5, NULL, 30, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-09-27 12:09:49.237105+00'),
	(127, 6, NULL, 30, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-09-27 13:09:49.237105+00'),
	(128, 7, NULL, 30, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-09-27 14:09:49.237105+00'),
	(129, 8, NULL, 30, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-09-27 15:09:49.237105+00'),
	(130, 2, NULL, 31, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-20 13:09:49.237105+00'),
	(131, 10, NULL, 31, 10, 'REPORT_FIRST_CONFIRMED', 'Your report got its first confirmation', '2026-11-20 13:09:49.237105+00'),
	(132, 3, NULL, 31, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-20 14:09:49.237105+00'),
	(133, 3, NULL, 32, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-15 12:09:49.237105+00'),
	(134, 2, NULL, 32, 10, 'REPORT_FIRST_CONFIRMED', 'Your report got its first confirmation', '2026-11-15 12:09:49.237105+00'),
	(135, 5, NULL, 34, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-22 10:09:49.237105+00'),
	(136, 4, NULL, 34, 10, 'REPORT_FIRST_CONFIRMED', 'Your report got its first confirmation', '2026-11-22 10:09:49.237105+00'),
	(137, 5, NULL, 34, 25, 'CLEANED_SPOT', 'Exorcised a garbage spot', '2026-11-25 12:09:49.237105+00'),
	(138, 6, NULL, 35, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-21 09:09:49.237105+00'),
	(139, 5, NULL, 35, 10, 'REPORT_FIRST_CONFIRMED', 'Your report got its first confirmation', '2026-11-21 09:09:49.237105+00'),
	(140, 7, NULL, 35, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-21 10:09:49.237105+00'),
	(141, 8, NULL, 35, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-21 11:09:49.237105+00'),
	(142, 7, NULL, 36, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-13 13:09:49.237105+00'),
	(143, 6, NULL, 36, 10, 'REPORT_FIRST_CONFIRMED', 'Your report got its first confirmation', '2026-11-13 13:09:49.237105+00'),
	(144, 8, NULL, 36, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-13 14:09:49.237105+00'),
	(145, 9, NULL, 36, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-13 15:09:49.237105+00'),
	(146, 10, NULL, 36, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-13 16:09:49.237105+00'),
	(147, 2, NULL, 36, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-13 17:09:49.237105+00'),
	(148, 8, NULL, 37, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-07 12:09:49.237105+00'),
	(149, 7, NULL, 37, 10, 'REPORT_FIRST_CONFIRMED', 'Your report got its first confirmation', '2026-11-07 12:09:49.237105+00'),
	(150, 9, NULL, 37, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-07 13:09:49.237105+00');
INSERT INTO public.score_events (id, user_id, entity_id, spot_id, points, reason, description, created_at) VALUES
	(151, 9, NULL, 38, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-10-24 11:09:49.237105+00'),
	(152, 8, NULL, 38, 10, 'REPORT_FIRST_CONFIRMED', 'Your report got its first confirmation', '2026-10-24 11:09:49.237105+00'),
	(153, 10, NULL, 38, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-10-24 12:09:49.237105+00'),
	(154, 2, NULL, 38, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-10-24 13:09:49.237105+00'),
	(155, 3, NULL, 38, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-10-24 14:09:49.237105+00'),
	(156, 4, NULL, 38, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-10-24 15:09:49.237105+00'),
	(157, 5, NULL, 38, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-10-24 16:09:49.237105+00'),
	(158, 6, NULL, 38, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-10-24 17:09:49.237105+00'),
	(159, 2, NULL, 40, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-17 09:09:49.237105+00'),
	(160, 10, NULL, 40, 10, 'REPORT_FIRST_CONFIRMED', 'Your report got its first confirmation', '2026-11-17 09:09:49.237105+00'),
	(161, 3, NULL, 40, 5, 'CONFIRMED_SPOT', 'Confirmed another hunter''s sighting', '2026-11-17 10:09:49.237105+00'),
	(162, 1, NULL, 40, 25, 'CLEANED_SPOT', 'Exorcised a garbage spot', '2026-11-22 12:09:49.237105+00'),
	(163, 2, NULL, 41, 25, 'CLEANED_SPOT', 'Exorcised a garbage spot', '2026-11-06 12:09:49.237105+00'),
	(164, 2, NULL, 42, 25, 'CLEANED_SPOT', 'Exorcised a garbage spot', '2026-11-11 12:09:49.237105+00'),
	(165, 2, NULL, 43, 25, 'CLEANED_SPOT', 'Exorcised a garbage spot', '2026-11-17 12:09:49.237105+00'),
	(166, NULL, 1, 1, -50, 'LINKED_SPOT_CURSED', 'Linked spot became cursed', '2026-11-24 08:09:49.237105+00'),
	(167, NULL, 1, 2, -50, 'LINKED_SPOT_CURSED', 'Linked spot became cursed', '2026-11-24 08:09:49.237105+00'),
	(168, NULL, 1, 3, -50, 'LINKED_SPOT_CURSED', 'Linked spot became cursed', '2026-11-24 08:09:49.237105+00'),
	(169, NULL, 1, 4, -50, 'LINKED_SPOT_CURSED', 'Linked spot became cursed', '2026-11-24 08:09:49.237105+00'),
	(170, NULL, 2, 29, -15, 'LINKED_SPOT_OVERDUE', 'Linked spot open longer than 7 days', '2026-11-11 09:09:49.237105+00'),
	(171, NULL, 4, 17, -15, 'LINKED_SPOT_OVERDUE', 'Linked spot open longer than 7 days', '2026-11-17 11:09:49.237105+00'),
	(172, NULL, 7, 36, -15, 'LINKED_SPOT_OVERDUE', 'Linked spot open longer than 7 days', '2026-11-20 12:09:49.237105+00'),
	(173, NULL, 1, 1, -15, 'LINKED_SPOT_OVERDUE', 'Linked spot open longer than 7 days', '2026-11-21 12:09:49.237105+00'),
	(174, NULL, 5, 11, -15, 'LINKED_SPOT_OVERDUE', 'Linked spot open longer than 7 days', '2026-11-23 12:09:49.237105+00'),
	(175, NULL, 1, 2, -15, 'LINKED_SPOT_OVERDUE', 'Linked spot open longer than 7 days', '2026-11-24 11:09:49.237105+00'),
	(176, NULL, 3, 23, -15, 'LINKED_SPOT_OVERDUE', 'Linked spot open longer than 7 days', '2026-11-25 10:09:49.237105+00');


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.users (id, name, email, password_hash, role, civic_score, entity_id, ward, created_at) VALUES
	(11, 'Sunita Menon', 'society@exorcity.demo', '$2a$10$BCFGWg842sax9XL8oxJ.D.nGuKP.pQZthiuWF0mJwq76f3vGLG.CC', 'SOCIETY_ADMIN', 600, 1, NULL, '2026-09-17 12:09:49.237105+00'),
	(13, 'Mohan Gowda', 'shop@exorcity.demo', '$2a$10$BCFGWg842sax9XL8oxJ.D.nGuKP.pQZthiuWF0mJwq76f3vGLG.CC', 'SHOP_OWNER', 600, 7, NULL, '2026-09-17 12:09:49.237105+00'),
	(14, 'Anil Kapoor', 'lakeview@exorcity.demo', '$2a$10$BCFGWg842sax9XL8oxJ.D.nGuKP.pQZthiuWF0mJwq76f3vGLG.CC', 'SOCIETY_ADMIN', 600, 2, NULL, '2026-09-17 12:09:49.237105+00'),
	(1, 'Asha Rao', 'citizen@exorcity.demo', '$2a$10$BCFGWg842sax9XL8oxJ.D.nGuKP.pQZthiuWF0mJwq76f3vGLG.CC', 'CITIZEN', 655, NULL, NULL, '2026-09-17 12:09:49.237105+00'),
	(2, 'Ravi Kumar', 'hunter@exorcity.demo', '$2a$10$BCFGWg842sax9XL8oxJ.D.nGuKP.pQZthiuWF0mJwq76f3vGLG.CC', 'CITIZEN', 790, NULL, NULL, '2026-09-17 12:09:49.237105+00'),
	(3, 'Meera Nair', 'meera@exorcity.demo', '$2a$10$BCFGWg842sax9XL8oxJ.D.nGuKP.pQZthiuWF0mJwq76f3vGLG.CC', 'CITIZEN', 715, NULL, NULL, '2026-09-17 12:09:49.237105+00'),
	(4, 'Kiran Shetty', 'kiran@exorcity.demo', '$2a$10$BCFGWg842sax9XL8oxJ.D.nGuKP.pQZthiuWF0mJwq76f3vGLG.CC', 'CITIZEN', 700, NULL, NULL, '2026-09-17 12:09:49.237105+00'),
	(5, 'Arjun Reddy', 'arjun@exorcity.demo', '$2a$10$BCFGWg842sax9XL8oxJ.D.nGuKP.pQZthiuWF0mJwq76f3vGLG.CC', 'CITIZEN', 735, NULL, NULL, '2026-09-17 12:09:49.237105+00'),
	(6, 'Divya Prakash', 'divya@exorcity.demo', '$2a$10$BCFGWg842sax9XL8oxJ.D.nGuKP.pQZthiuWF0mJwq76f3vGLG.CC', 'CITIZEN', 700, NULL, NULL, '2026-09-17 12:09:49.237105+00'),
	(7, 'Farhan Ali', 'farhan@exorcity.demo', '$2a$10$BCFGWg842sax9XL8oxJ.D.nGuKP.pQZthiuWF0mJwq76f3vGLG.CC', 'CITIZEN', 705, NULL, NULL, '2026-09-17 12:09:49.237105+00'),
	(8, 'Lakshmi Hegde', 'lakshmi@exorcity.demo', '$2a$10$BCFGWg842sax9XL8oxJ.D.nGuKP.pQZthiuWF0mJwq76f3vGLG.CC', 'CITIZEN', 720, NULL, NULL, '2026-09-17 12:09:49.237105+00'),
	(9, 'Naveen Bhat', 'naveen@exorcity.demo', '$2a$10$BCFGWg842sax9XL8oxJ.D.nGuKP.pQZthiuWF0mJwq76f3vGLG.CC', 'CITIZEN', 690, NULL, NULL, '2026-09-17 12:09:49.237105+00'),
	(10, 'Priya Das', 'priya@exorcity.demo', '$2a$10$BCFGWg842sax9XL8oxJ.D.nGuKP.pQZthiuWF0mJwq76f3vGLG.CC', 'CITIZEN', 685, NULL, NULL, '2026-09-17 12:09:49.237105+00'),
	(12, 'Vikram Shah', 'corporate@exorcity.demo', '$2a$10$BCFGWg842sax9XL8oxJ.D.nGuKP.pQZthiuWF0mJwq76f3vGLG.CC', 'CORPORATE_ADMIN', 625, 4, NULL, '2026-09-17 12:09:49.237105+00'),
	(15, 'Officer Rekha Iyer', 'ward@exorcity.demo', '$2a$10$BCFGWg842sax9XL8oxJ.D.nGuKP.pQZthiuWF0mJwq76f3vGLG.CC', 'WARD_OFFICER', 625, NULL, 'Koramangala', '2026-09-17 12:09:49.237105+00');


--
-- Name: confirmations_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.confirmations_id_seq', 119, true);


--
-- Name: garbage_spots_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.garbage_spots_id_seq', 43, true);


--
-- Name: responsible_entities_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.responsible_entities_id_seq', 8, true);


--
-- Name: score_events_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.score_events_id_seq', 176, true);


--
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.users_id_seq', 15, true);


--
-- Name: confirmations confirmations_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.confirmations
    ADD CONSTRAINT confirmations_pkey PRIMARY KEY (id);


--
-- Name: flyway_schema_history flyway_schema_history_pk; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.flyway_schema_history
    ADD CONSTRAINT flyway_schema_history_pk PRIMARY KEY (installed_rank);


--
-- Name: garbage_spots garbage_spots_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.garbage_spots
    ADD CONSTRAINT garbage_spots_pkey PRIMARY KEY (id);


--
-- Name: responsible_entities responsible_entities_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.responsible_entities
    ADD CONSTRAINT responsible_entities_pkey PRIMARY KEY (id);


--
-- Name: score_events score_events_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.score_events
    ADD CONSTRAINT score_events_pkey PRIMARY KEY (id);


--
-- Name: confirmations uq_confirmation_spot_user; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.confirmations
    ADD CONSTRAINT uq_confirmation_spot_user UNIQUE (spot_id, user_id);


--
-- Name: users users_email_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_email_key UNIQUE (email);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: flyway_schema_history_s_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX flyway_schema_history_s_idx ON public.flyway_schema_history USING btree (success);


--
-- Name: idx_score_events_entity; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_score_events_entity ON public.score_events USING btree (entity_id);


--
-- Name: idx_score_events_user; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_score_events_user ON public.score_events USING btree (user_id);


--
-- Name: idx_spots_entity; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_spots_entity ON public.garbage_spots USING btree (linked_entity_id);


--
-- Name: idx_spots_lat_lng; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_spots_lat_lng ON public.garbage_spots USING btree (latitude, longitude);


--
-- Name: idx_spots_status; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_spots_status ON public.garbage_spots USING btree (status);


--
-- Name: idx_spots_ward; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_spots_ward ON public.garbage_spots USING btree (ward);


--
-- Name: confirmations confirmations_spot_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.confirmations
    ADD CONSTRAINT confirmations_spot_id_fkey FOREIGN KEY (spot_id) REFERENCES public.garbage_spots(id) ON DELETE CASCADE;


--
-- Name: confirmations confirmations_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.confirmations
    ADD CONSTRAINT confirmations_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: garbage_spots garbage_spots_cleaned_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.garbage_spots
    ADD CONSTRAINT garbage_spots_cleaned_by_fkey FOREIGN KEY (cleaned_by) REFERENCES public.users(id);


--
-- Name: garbage_spots garbage_spots_linked_entity_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.garbage_spots
    ADD CONSTRAINT garbage_spots_linked_entity_id_fkey FOREIGN KEY (linked_entity_id) REFERENCES public.responsible_entities(id);


--
-- Name: garbage_spots garbage_spots_reported_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.garbage_spots
    ADD CONSTRAINT garbage_spots_reported_by_fkey FOREIGN KEY (reported_by) REFERENCES public.users(id);


--
-- Name: score_events score_events_entity_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.score_events
    ADD CONSTRAINT score_events_entity_id_fkey FOREIGN KEY (entity_id) REFERENCES public.responsible_entities(id);


--
-- Name: score_events score_events_spot_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.score_events
    ADD CONSTRAINT score_events_spot_id_fkey FOREIGN KEY (spot_id) REFERENCES public.garbage_spots(id) ON DELETE SET NULL;


--
-- Name: score_events score_events_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.score_events
    ADD CONSTRAINT score_events_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: users users_entity_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_entity_id_fkey FOREIGN KEY (entity_id) REFERENCES public.responsible_entities(id);


--
-- PostgreSQL database dump complete
--

\unrestrict 1hSG1euIu6gvYzHAMDZrgQtfgjyUe0IrDpO3OX4WPzwXGIPtfPej6gTktQ9uIuq


COMMIT;
