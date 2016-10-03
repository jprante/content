package org.xbib.content.language.enums;

import org.xbib.content.language.Subtag;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Enum constants used to validate language tags.
 */
public enum Language implements LanguageConstants {

    AA(null, null, null, "Afar"), AB(null, null, null, "Abkhazian"), AE(null, null, null, "Avestan"), AF(null, null,
    null, "Afrikaans"), AK(null, null, null, "Akan"), AM(null, null, null, "Amharic"), AN(null, null, null,
    "Aragonese"), ANP(null, null, null, "Angika"), AR(null, null, null, "Arabic"), AS(null, null, null, "Assamese"), AV(
    null, null, null, "Avaric"), AY(null, null, null, "Aymara"), AZ(null, null, null, "Azerbaijani"), BA(null,
    null, null, "Bashkir"), BE(null, null, null, "Belarusian"), BG(null, null, null, "Bulgarian"), BH(null, null,
    null, "Bihari"), BI(null, null, null, "Bislama"), BM(null, null, null, "Bambara"), BN(null, null, null,
    "Bengali"), BO(null, null, null, "Tibetan"), BR(null, null, null, "Breton"), BS(null, null, null, "Bosnian"), CA(
    null, null, null, "Catalan", "Valencian"), CE(null, null, null, "Chechen"), CH(null, null, null, "Chamorro"), CO(
    null, null, null, "Corsican"), CR(null, null, null, "Cree"), CS(null, null, null, "Czech"), CU(null, null,
    null, "Church Slavic", "Old Slavonic", "Church Slavonic", "Old Bulgarian", "Old Church Slavonic"), CV(null,
    null, null, "Chuvash"), CY(null, null, null, "Welsh"), DA(null, null, null, "Danish"), DE(null, null, null,
    "German"), DV(null, null, null, "Divehi", "Dhivehi", "Maldivian"), DZ(null, null, null, "Dzongkha"), EE(null,
    null, null, "Ewe"), EL(null, null, null, "Greek, Modern (1453-)"), EN(null, null, null, "English"), EO(null,
    null, null, "Esperanto"), ES(null, null, null, "Spanish", "Castilian"), ET(null, null, null, "Estonian"), EU(
    null, null, null, "Basque"), FA(null, null, null, "Persian"), FF(null, null, null, "Fulah"), FI(null, null,
    null, "Finnish"), FJ(null, null, null, "Fijian"), FO(null, null, null, "Faroese"), FR(null, null, null,
    "French"), FRR(null, null, null, "Northern Frisian"), FY(null, null, null, "Western Frisian"), GA(null, null,
    null, "Irish"), GD(null, null, null, "Gaelic", "Scottish Gaelic"), GL(null, null, null, "Galician"), GN(null,
    null, null, "Guarani"), GU(null, null, null, "Gujarati"), GV(null, null, null, "Manx"), HA(null, null, null,
    "Hausa"), HE(null, null, null, "Hebrew"), HI(null, null, null, "Hindi"), HO(null, null, null, "Hiri Motu"), HR(
    null, null, null, "Croatian"), HT(null, null, null, "Haitian", "Haitian Creole"), HU(null, null, null,
    "Hungarian"), HY(null, null, null, "Armenian"), HZ(null, null, null, "Herero"), IA(null, null, null,
    "Interlingua (International Auxiliary Language Association)"), ID(null, null, null, "Indonesian"), IE(null,
    null, null, "Interlingue", "Occidental"), IG(null, null, null, "Igbo"), II(null, null, null, "Sichuan Yi",
    "Nuosu"), IK(null, null, null, "Inupiaq"), IN("1989-01-01", "id", null, "Indonesian"), IO(null, null, null,
    "Ido"), IS(null, null, null, "Icelandic"), IT(null, null, null, "Italian"), IU(null, null, null, "Inuktitut"), IW(
    "1989-01-01", "he", null, "Hebrew"), JA(null, null, null, "Japanese"), JI("1989-01-01", "yi", null, "Yiddish"), JV(
    null, null, null, "Javanese"), JW("2001-08-13", "jv", null, "Javanese"), KA(null, null, null, "Georgian"), KG(
    null, null, null, "Kongo"), KI(null, null, null, "Kikuyu", "Gikuyu"), KJ(null, null, null, "Kuanyama",
    "Kwanyama"), KK(null, null, null, "Kazakh"), KL(null, null, null, "Kalaallisut", "Greenlandic"), KM(null, null,
    null, "Central Khmer"), KN(null, null, null, "Kannada"), KO(null, null, null, "Korean"), KR(null, null, null,
    "Kanuri"), KS(null, null, null, "Kashmiri"), KU(null, null, null, "Kurdish"), KV(null, null, null, "Komi"), KW(
    null, null, null, "Cornish"), KY(null, null, null, "Kyrgyz", "Kirghiz"), LA(null, null, null, "Latin"), LB(
    null, null, null, "Luxembourgish", "Letzeburgesch"), LG(null, null, null, "Ganda"), LI(null, null, null,
    "Limburgan", "Limburger", "Limburgish"), LN(null, null, null, "Lingala"), LO(null, null, null, "Lao"), LT(null,
    null, null, "Lithuanian"), LU(null, null, null, "Luba-Katanga"), LV(null, null, null, "Latvian"), MG(null,
    null, null, "Malagasy"), MH(null, null, null, "Marshallese"), MI(null, null, null, "Maori"), MK(null, null,
    null, "Macedonian"), ML(null, null, null, "Malayalam"), MN(null, null, null, "Mongolian"), MO(null, null, null,
    "Moldavian"), MR(null, null, null, "Marathi"), MS(null, null, null, "Malay"), MT(null, null, null, "Maltese"), MY(
    null, null, null, "Burmese"), NA(null, null, null, "Nauru"), NB(null, null, null, "Norwegian Bokm&#xE5;l"), ND(
    null, null, null, "Ndebele, North", "North Ndebele"), NE(null, null, null, "Nepali"), NG(null, null, null,
    "Ndonga"), NL(null, null, null, "Dutch", "Flemish"), NN(null, null, null, "Norwegian Nynorsk"), NO(null, null,
    null, "Norwegian"), NR(null, null, null, "Ndebele, South", "South Ndebele"), NV(null, null, null, "Navajo",
    "Navaho"), NY(null, null, null, "Chichewa", "Chewa", "Nyanja"), OC(null, null, null, "Occitan (post 1500)",
    "Proven&#xE7;al"), OJ(null, null, null, "Ojibwa"), OM(null, null, null, "Oromo"), OR(null, null, null, "Oriya"), OS(
    null, null, null, "Ossetian", "Ossetic"), PA(null, null, null, "Panjabi", "Punjabi"), PI(null, null, null,
    "Pali"), PL(null, null, null, "Polish"), PS(null, null, null, "Pushto"), PT(null, null, null, "Portuguese"), QU(
    null, null, null, "Quechua"), RM(null, null, null, "Romansh"), RN(null, null, null, "Rundi"), RO(null, null,
    null, "Romanian"), RU(null, null, null, "Russian"), RW(null, null, null, "Kinyarwanda"), SA(null, null, null,
    "Sanskrit"), SC(null, null, null, "Sardinian"), SD(null, null, null, "Sindhi"), SE(null, null, null,
    "Northern Sami"), SG(null, null, null, "Sango"), SH("2000-02-18", null, null, "Serbo-Croatian"), SI(null, null,
    null, "Sinhala", "Sinhalese"), SK(null, null, null, "Slovak"), SL(null, null, null, "Slovenian"), SM(null,
    null, null, "Samoan"), SN(null, null, null, "Shona"), SO(null, null, null, "Somali"), SQ(null, null, null,
    "Albanian"), SR(null, null, null, "Serbian"), SS(null, null, null, "Swati"), ST(null, null, null,
    "Sotho, Southern"), SU(null, null, null, "Sundanese"), SV(null, null, null, "Swedish"), SW(null, null, null,
    "Swahili"), TA(null, null, null, "Tamil"), TE(null, null, null, "Telugu"), TG(null, null, null, "Tajik"), TH(
    null, null, null, "Thai"), TI(null, null, null, "Tigrinya"), TK(null, null, null, "Turkmen"), TL(null, null,
    null, "Tagalog"), TN(null, null, null, "Tswana"), TO(null, null, null, "Tonga (Tonga Islands)"), TR(null, null,
    null, "Turkish"), TS(null, null, null, "Tsonga"), TT(null, null, null, "Tatar"), TW(null, null, null, "Twi"), TY(
    null, null, null, "Tahitian"), UG(null, null, null, "Uighur", "Uyghur"), UK(null, null, null, "Ukrainian"), UR(
    null, null, null, "Urdu"), UZ(null, null, null, "Uzbek"), VE(null, null, null, "Venda"), VI(null, null, null,
    "Vietnamese"), VO(null, null, null, "Volap&#xFC;k"), WA(null, null, null, "Walloon"), WO(null, null, null,
    "Wolof"), XH(null, null, null, "Xhosa"), YI(null, null, null, "Yiddish"), YO(null, null, null, "Yoruba"), ZA(
    null, null, null, "Zhuang", "Chuang"), ZH(null, null, null, "Chinese"), ZU(null, null, null, "Zulu"), ACE(null,
    null, null, "Achinese"), ACH(null, null, null, "Acoli"), ADA(null, null, null, "Adangme"), ADY(null, null,
    null, "Adyghe", "Adygei"), AFA(null, null, null, "Afro-Asiatic (Other)"), AFH(null, null, null, "Afrihili"), AIN(
    null, null, null, "Ainu"), AKK(null, null, null, "Akkadian"), ALE(null, null, null, "Aleut"), ALG(null, null,
    null, "Algonquian languages"), ALT(null, null, null, "Southern Altai"), ANG(null, null, null,
    "English, Old (ca. 450-1100)"), APA(null, null, null, "Apache languages"), ARC(null, null, null,
    "Official Aramaic (700-300 BCE)", "Imperial Aramaic (700-300 BCE)"), ARN(null, null, null, "Mapudungun",
    "Mapuche"), ARP(null, null, null, "Arapaho"), ART(null, null, null, "Artificial (Other)"), ARW(null, null,
    null, "Arawak"), AST(null, null, null, "Asturian", "Bable", "Leonese", "Asturleonese"), ATH(null, null, null,
    "Athapascan languages"), AUS(null, null, null, "Australian languages"), AWA(null, null, null, "Awadhi"), BAD(
    null, null, null, "Banda languages"), BAI(null, null, null, "Bamileke languages"), BAL(null, null, null,
    "Baluchi"), BAN(null, null, null, "Balinese"), BAS(null, null, null, "Basa"), BAT(null, null, null,
    "Baltic (Other)"), BEJ(null, null, null, "Beja", "Bedawiyet"), BEM(null, null, null, "Bemba"), BER(null, null,
    null, "Berber (Other)"), BHO(null, null, null, "Bhojpuri"), BIK(null, null, null, "Bikol"), BIN(null, null,
    null, "Bini", "Edo"), BLA(null, null, null, "Siksika"), BNT(null, null, null, "Bantu (Other)"), BRA(null, null,
    null, "Braj"), BTK(null, null, null, "Batak languages"), BUA(null, null, null, "Buriat"), BUG(null, null, null,
    "Buginese"), BYN(null, null, null, "Blin", "Bilin"), CAD(null, null, null, "Caddo"), CAI(null, null, null,
    "Central American Indian (Other)"), CAR(null, null, null, "Galibi Carib"), CAU(null, null, null,
    "Caucasian (Other)"), CEB(null, null, null, "Cebuano"), CEL(null, null, null, "Celtic (Other)"), CHB(null,
    null, null, "Chibcha"), CHG(null, null, null, "Chagatai"), CHK(null, null, null, "Chuukese"), CHM(null, null,
    null, "Mari"), CHN(null, null, null, "Chinook jargon"), CHO(null, null, null, "Choctaw"), CHP(null, null, null,
    "Chipewyan", "Dene Suline"), CHR(null, null, null, "Cherokee"), CHY(null, null, null, "Cheyenne"), CMC(null,
    null, null, "Chamic languages"), COP(null, null, null, "Coptic"), CPE(null, null, null,
    "Creoles and pidgins, English-based (Other)"), CPF(null, null, null,
    "Creoles and pidgins, French-based (Other)"), CPP(null, null, null,
    "Creoles and pidgins, Portuguese-based (Other)"), CRH(null, null, null, "Crimean Tatar", "Crimean Turkish"), CRP(
    null, null, null, "Creoles and pidgins (Other)"), CSB(null, null, null, "Kashubian"), CUS(null, null, null,
    "Cushitic (Other)"), DAK(null, null, null, "Dakota"), DAR(null, null, null, "Dargwa"), DAY(null, null, null,
    "Land Dayak languages"), DEL(null, null, null, "Delaware"), DEN(null, null, null, "Slave (Athapascan)"), DGR(
    null, null, null, "Dogrib"), DIN(null, null, null, "Dinka"), DOI(null, null, null, "Dogri"), DRA(null, null,
    null, "Dravidian (Other)"), DSB(null, null, null, "Lower Sorbian"), DUA(null, null, null, "Duala"), DUM(null,
    null, null, "Dutch, Middle (ca. 1050-1350)"), DYU(null, null, null, "Dyula"), EFI(null, null, null, "Efik"), EGY(
    null, null, null, "Egyptian (Ancient)"), EKA(null, null, null, "Ekajuk"), ELX(null, null, null, "Elamite"), ENM(
    null, null, null, "English, Middle (1100-1500)"), EWO(null, null, null, "Ewondo"), FAN(null, null, null, "Fang"), FAT(
    null, null, null, "Fanti"), FIL(null, null, null, "Filipino", "Pilipino"), FIU(null, null, null,
    "Finno-Ugrian (Other)"), FON(null, null, null, "Fon"), FRM(null, null, null, "French, Middle (ca. 1400-1600)"), FRO(
    null, null, null, "French, Old (842-ca. 1400)"), FRS(null, null, null, "Eastern Frisian"), FUR(null, null,
    null, "Friulian"), GAA(null, null, null, "Ga"), GAY(null, null, null, "Gayo"), GBA(null, null, null, "Gbaya"), GEM(
    null, null, null, "Germanic (Other)"), GEZ(null, null, null, "Geez"), GIL(null, null, null, "Gilbertese"), GMH(
    null, null, null, "German, Middle High (ca. 1050-1500)"), GOH(null, null, null,
    "German, Old High (ca. 750-1050)"), GON(null, null, null, "Gondi"), GOR(null, null, null, "Gorontalo"), GOT(
    null, null, null, "Gothic"), GRB(null, null, null, "Grebo"), GRC(null, null, null, "Greek, Ancient (to 1453)"), GSW(
    null, null, null, "Swiss German", "Alemannic"), GWI(null, null, null, "Gwich&#xB4;in"), HAI(null, null, null,
    "Haida"), HAW(null, null, null, "Hawaiian"), HIL(null, null, null, "Hiligaynon"), HIM(null, null, null,
    "Himachali"), HIT(null, null, null, "Hittite"), HMN(null, null, null, "Hmong"), HSB(null, null, null,
    "Upper Sorbian"), HUP(null, null, null, "Hupa"), IBA(null, null, null, "Iban"), IJO(null, null, null,
    "Ijo languages"), ILO(null, null, null, "Iloko"), INC(null, null, null, "Indic (Other)"), INE(null, null, null,
    "Indo-European (Other)"), INH(null, null, null, "Ingush"), IRA(null, null, null, "Iranian (Other)"), IRO(null,
    null, null, "Iroquoian languages"), JBO(null, null, null, "Lojban"), JPR(null, null, null, "Judeo-Persian"), JRB(
    null, null, null, "Judeo-Arabic"), KAA(null, null, null, "Kara-Kalpak"), KAB(null, null, null, "Kabyle"), KAC(
    null, null, null, "Kachin", "Jingpho"), KAM(null, null, null, "Kamba"), KAR(null, null, null, "Karen languages"), KAW(
    null, null, null, "Kawi"), KBD(null, null, null, "Kabardian"), KHA(null, null, null, "Khasi"), KHI(null, null,
    null, "Khoisan (Other)"), KHO(null, null, null, "Khotanese"), KMB(null, null, null, "Kimbundu"), KOK(null,
    null, null, "Konkani"), KOS(null, null, null, "Kosraean"), KPE(null, null, null, "Kpelle"), KRC(null, null,
    null, "Karachay-Balkar"), KRL(null, null, null, "Karelian"), KRO(null, null, null, "Kru languages"), KRU(null,
    null, null, "Kurukh"), KUM(null, null, null, "Kumyk"), KUT(null, null, null, "Kutenai"), LAD(null, null, null,
    "Ladino"), LAH(null, null, null, "Lahnda"), LAM(null, null, null, "Lamba"), LEZ(null, null, null, "Lezghian"), LOL(
    null, null, null, "Mongo"), LOZ(null, null, null, "Lozi"), LUA(null, null, null, "Luba-Lulua"), LUI(null, null,
    null, "Luiseno"), LUN(null, null, null, "Lunda"), LUO(null, null, null, "Luo (Kenya and Tanzania)"), LUS(null,
    null, null, "Lushai"), MAD(null, null, null, "Madurese"), MAG(null, null, null, "Magahi"), MAI(null, null,
    null, "Maithili"), MAK(null, null, null, "Makasar"), MAN(null, null, null, "Mandingo"), MAP(null, null, null,
    "Austronesian (Other)"), MAS(null, null, null, "Masai"), MDF(null, null, null, "Moksha"), MDR(null, null, null,
    "Mandar"), MEN(null, null, null, "Mende"), MGA(null, null, null, "Irish, Middle (900-1200)"), MIC(null, null,
    null, "Mi'kmaq", "Micmac"), MIN(null, null, null, "Minangkabau"), MIS(null, null, null, "Uncoded languages"), MKH(
    null, null, null, "Mon-Khmer (Other)"), MNC(null, null, null, "Manchu"), MNI(null, null, null, "Manipuri"), MNO(
    null, null, null, "Manobo languages"), MOH(null, null, null, "Mohawk"), MOS(null, null, null, "Mossi"), MUL(
    null, null, null, "Multiple languages"), MUN(null, null, null, "Munda languages"), MUS(null, null, null,
    "Creek"), MWL(null, null, null, "Mirandese"), MWR(null, null, null, "Marwari"), MYN(null, null, null,
    "Mayan languages"), MYV(null, null, null, "Erzya"), NAH(null, null, null, "Nahuatl languages"), NAI(null, null,
    null, "North American Indian"), NAP(null, null, null, "Neapolitan"), NDS(null, null, null, "Low German",
    "Low Saxon", "German, Low", "Saxon, Low"), NEW(null, null, null, "Nepal Bhasa", "Newari"), NIA(null, null,
    null, "Nias"), NIC(null, null, null, "Niger-Kordofanian (Other)"), NIU(null, null, null, "Niuean"), NOG(null,
    null, null, "Nogai"), NON(null, null, null, "Norse, Old"), NQO(null, null, null, "N&#x2019;Ko"), NSO(null,
    null, null, "Northern Sotho", "Pedi", "Sepedi"), NUB(null, null, null, "Nubian languages"), NWC(null, null,
    null, "Classical Newari", "Old Newari", "Classical Nepal Bhasa"), NYM(null, null, null, "Nyamwezi"), NYN(null,
    null, null, "Nyankole"), NYO(null, null, null, "Nyoro"), NZI(null, null, null, "Nzima"), OSA(null, null, null,
    "Osage"), OTA(null, null, null, "Turkish, Ottoman (1500-1928)"), OTO(null, null, null, "Otomian languages"), PAA(
    null, null, null, "Papuan (Other)"), PAG(null, null, null, "Pangasinan"), PAL(null, null, null, "Pahlavi"), PAM(
    null, null, null, "Pampanga", "Kapampangan"), PAP(null, null, null, "Papiamento"), PAU(null, null, null,
    "Palauan"), PEO(null, null, null, "Persian, Old (ca. 600-400 B.C.)"), PHI(null, null, null,
    "Philippine (Other)"), PHN(null, null, null, "Phoenician"), PON(null, null, null, "Pohnpeian"), PRA(null, null,
    null, "Prakrit languages"), PRO(null, null, null, "Proven&#xE7;al, Old (to 1500)"), QAA(null, null, null,
    PRIVATE), QAB(null, null, null, PRIVATE), QAC(null, null, null, PRIVATE), QAD(null, null,
    null, PRIVATE), QAE(null, null, null, PRIVATE), QAF(null, null, null, PRIVATE), QAG(null,
    null, null, PRIVATE), QAH(null, null, null, PRIVATE), QAI(null, null, null, PRIVATE), QAJ(
    null, null, null, PRIVATE), QAK(null, null, null, PRIVATE), QAL(null, null, null, PRIVATE), QAM(
    null, null, null, PRIVATE), QAN(null, null, null, PRIVATE), QAO(null, null, null, PRIVATE), QAP(
    null, null, null, PRIVATE), QAQ(null, null, null, PRIVATE), QAR(null, null, null, PRIVATE), QAS(
    null, null, null, PRIVATE), QAT(null, null, null, PRIVATE), QAU(null, null, null, PRIVATE), QAV(
    null, null, null, PRIVATE), QAW(null, null, null, PRIVATE), QAX(null, null, null, PRIVATE), QAY(
    null, null, null, PRIVATE), QAZ(null, null, null, PRIVATE), QBA(null, null, null, PRIVATE), QBB(
    null, null, null, PRIVATE), QBC(null, null, null, PRIVATE), QBD(null, null, null, PRIVATE), QBE(
    null, null, null, PRIVATE), QBF(null, null, null, PRIVATE), QBG(null, null, null, PRIVATE), QBH(
    null, null, null, PRIVATE), QBI(null, null, null, PRIVATE), QBJ(null, null, null, PRIVATE), QBK(
    null, null, null, PRIVATE), QBL(null, null, null, PRIVATE), QBM(null, null, null, PRIVATE), QBN(
    null, null, null, PRIVATE), QBO(null, null, null, PRIVATE), QBP(null, null, null, PRIVATE), QBQ(
    null, null, null, PRIVATE), QBR(null, null, null, PRIVATE), QBS(null, null, null, PRIVATE), QBT(
    null, null, null, PRIVATE), QBU(null, null, null, PRIVATE), QBV(null, null, null, PRIVATE), QBW(
    null, null, null, PRIVATE), QBX(null, null, null, PRIVATE), QBY(null, null, null, PRIVATE), QBZ(
    null, null, null, PRIVATE), QCA(null, null, null, PRIVATE), QCB(null, null, null, PRIVATE), QCC(
    null, null, null, PRIVATE), QCD(null, null, null, PRIVATE), QCE(null, null, null, PRIVATE), QCF(
    null, null, null, PRIVATE), QCG(null, null, null, PRIVATE), QCH(null, null, null, PRIVATE), QCI(
    null, null, null, PRIVATE), QCJ(null, null, null, PRIVATE), QCK(null, null, null, PRIVATE), QCL(
    null, null, null, PRIVATE), QCM(null, null, null, PRIVATE), QCN(null, null, null, PRIVATE), QCO(
    null, null, null, PRIVATE), QCP(null, null, null, PRIVATE), QCQ(null, null, null, PRIVATE), QCR(
    null, null, null, PRIVATE), QCS(null, null, null, PRIVATE), QCT(null, null, null, PRIVATE), QCU(
    null, null, null, PRIVATE), QCV(null, null, null, PRIVATE), QCW(null, null, null, PRIVATE), QCX(
    null, null, null, PRIVATE), QCY(null, null, null, PRIVATE), QCZ(null, null, null, PRIVATE), QDA(
    null, null, null, PRIVATE), QDB(null, null, null, PRIVATE), QDC(null, null, null, PRIVATE), QDD(
    null, null, null, PRIVATE), QDE(null, null, null, PRIVATE), QDF(null, null, null, PRIVATE), QDG(
    null, null, null, PRIVATE), QDH(null, null, null, PRIVATE), QDI(null, null, null, PRIVATE), QDJ(
    null, null, null, PRIVATE), QDK(null, null, null, PRIVATE), QDL(null, null, null, PRIVATE), QDM(
    null, null, null, PRIVATE), QDN(null, null, null, PRIVATE), QDO(null, null, null, PRIVATE), QDP(
    null, null, null, PRIVATE), QDQ(null, null, null, PRIVATE), QDR(null, null, null, PRIVATE), QDS(
    null, null, null, PRIVATE), QDT(null, null, null, PRIVATE), QDU(null, null, null, PRIVATE), QDV(
    null, null, null, PRIVATE), QDW(null, null, null, PRIVATE), QDX(null, null, null, PRIVATE), QDY(
    null, null, null, PRIVATE), QDZ(null, null, null, PRIVATE), QEA(null, null, null, PRIVATE), QEB(
    null, null, null, PRIVATE), QEC(null, null, null, PRIVATE), QED(null, null, null, PRIVATE), QEE(
    null, null, null, PRIVATE), QEF(null, null, null, PRIVATE), QEG(null, null, null, PRIVATE), QEH(
    null, null, null, PRIVATE), QEI(null, null, null, PRIVATE), QEJ(null, null, null, PRIVATE), QEK(
    null, null, null, PRIVATE), QEL(null, null, null, PRIVATE), QEM(null, null, null, PRIVATE), QEN(
    null, null, null, PRIVATE), QEO(null, null, null, PRIVATE), QEP(null, null, null, PRIVATE), QEQ(
    null, null, null, PRIVATE), QER(null, null, null, PRIVATE), QES(null, null, null, PRIVATE), QET(
    null, null, null, PRIVATE), QEU(null, null, null, PRIVATE), QEV(null, null, null, PRIVATE), QEW(
    null, null, null, PRIVATE), QEX(null, null, null, PRIVATE), QEY(null, null, null, PRIVATE), QEZ(
    null, null, null, PRIVATE), QFA(null, null, null, PRIVATE), QFB(null, null, null, PRIVATE), QFC(
    null, null, null, PRIVATE), QFD(null, null, null, PRIVATE), QFE(null, null, null, PRIVATE), QFF(
    null, null, null, PRIVATE), QFG(null, null, null, PRIVATE), QFH(null, null, null, PRIVATE), QFI(
    null, null, null, PRIVATE), QFJ(null, null, null, PRIVATE), QFK(null, null, null, PRIVATE), QFL(
    null, null, null, PRIVATE), QFM(null, null, null, PRIVATE), QFN(null, null, null, PRIVATE), QFO(
    null, null, null, PRIVATE), QFP(null, null, null, PRIVATE), QFQ(null, null, null, PRIVATE), QFR(
    null, null, null, PRIVATE), QFS(null, null, null, PRIVATE), QFT(null, null, null, PRIVATE), QFU(
    null, null, null, PRIVATE), QFV(null, null, null, PRIVATE), QFW(null, null, null, PRIVATE), QFX(
    null, null, null, PRIVATE), QFY(null, null, null, PRIVATE), QFZ(null, null, null, PRIVATE), QGA(
    null, null, null, PRIVATE), QGB(null, null, null, PRIVATE), QGC(null, null, null, PRIVATE), QGD(
    null, null, null, PRIVATE), QGE(null, null, null, PRIVATE), QGF(null, null, null, PRIVATE), QGG(
    null, null, null, PRIVATE), QGH(null, null, null, PRIVATE), QGI(null, null, null, PRIVATE), QGJ(
    null, null, null, PRIVATE), QGK(null, null, null, PRIVATE), QGL(null, null, null, PRIVATE), QGM(
    null, null, null, PRIVATE), QGN(null, null, null, PRIVATE), QGO(null, null, null, PRIVATE), QGP(
    null, null, null, PRIVATE), QGQ(null, null, null, PRIVATE), QGR(null, null, null, PRIVATE), QGS(
    null, null, null, PRIVATE), QGT(null, null, null, PRIVATE), QGU(null, null, null, PRIVATE), QGV(
    null, null, null, PRIVATE), QGW(null, null, null, PRIVATE), QGX(null, null, null, PRIVATE), QGY(
    null, null, null, PRIVATE), QGZ(null, null, null, PRIVATE), QHA(null, null, null, PRIVATE), QHB(
    null, null, null, PRIVATE), QHC(null, null, null, PRIVATE), QHD(null, null, null, PRIVATE), QHE(
    null, null, null, PRIVATE), QHF(null, null, null, PRIVATE), QHG(null, null, null, PRIVATE), QHH(
    null, null, null, PRIVATE), QHI(null, null, null, PRIVATE), QHJ(null, null, null, PRIVATE), QHK(
    null, null, null, PRIVATE), QHL(null, null, null, PRIVATE), QHM(null, null, null, PRIVATE), QHN(
    null, null, null, PRIVATE), QHO(null, null, null, PRIVATE), QHP(null, null, null, PRIVATE), QHQ(
    null, null, null, PRIVATE), QHR(null, null, null, PRIVATE), QHS(null, null, null, PRIVATE), QHT(
    null, null, null, PRIVATE), QHU(null, null, null, PRIVATE), QHV(null, null, null, PRIVATE), QHW(
    null, null, null, PRIVATE), QHX(null, null, null, PRIVATE), QHY(null, null, null, PRIVATE), QHZ(
    null, null, null, PRIVATE), QIA(null, null, null, PRIVATE), QIB(null, null, null, PRIVATE), QIC(
    null, null, null, PRIVATE), QID(null, null, null, PRIVATE), QIE(null, null, null, PRIVATE), QIF(
    null, null, null, PRIVATE), QIG(null, null, null, PRIVATE), QIH(null, null, null, PRIVATE), QII(
    null, null, null, PRIVATE), QIJ(null, null, null, PRIVATE), QIK(null, null, null, PRIVATE), QIL(
    null, null, null, PRIVATE), QIM(null, null, null, PRIVATE), QIN(null, null, null, PRIVATE), QIO(
    null, null, null, PRIVATE), QIP(null, null, null, PRIVATE), QIQ(null, null, null, PRIVATE), QIR(
    null, null, null, PRIVATE), QIS(null, null, null, PRIVATE), QIT(null, null, null, PRIVATE), QIU(
    null, null, null, PRIVATE), QIV(null, null, null, PRIVATE), QIW(null, null, null, PRIVATE), QIX(
    null, null, null, PRIVATE), QIY(null, null, null, PRIVATE), QIZ(null, null, null, PRIVATE), QJA(
    null, null, null, PRIVATE), QJB(null, null, null, PRIVATE), QJC(null, null, null, PRIVATE), QJD(
    null, null, null, PRIVATE), QJE(null, null, null, PRIVATE), QJF(null, null, null, PRIVATE), QJG(
    null, null, null, PRIVATE), QJH(null, null, null, PRIVATE), QJI(null, null, null, PRIVATE), QJJ(
    null, null, null, PRIVATE), QJK(null, null, null, PRIVATE), QJL(null, null, null, PRIVATE), QJM(
    null, null, null, PRIVATE), QJN(null, null, null, PRIVATE), QJO(null, null, null, PRIVATE), QJP(
    null, null, null, PRIVATE), QJQ(null, null, null, PRIVATE), QJR(null, null, null, PRIVATE), QJS(
    null, null, null, PRIVATE), QJT(null, null, null, PRIVATE), QJU(null, null, null, PRIVATE), QJV(
    null, null, null, PRIVATE), QJW(null, null, null, PRIVATE), QJX(null, null, null, PRIVATE), QJY(
    null, null, null, PRIVATE), QJZ(null, null, null, PRIVATE), QKA(null, null, null, PRIVATE), QKB(
    null, null, null, PRIVATE), QKC(null, null, null, PRIVATE), QKD(null, null, null, PRIVATE), QKE(
    null, null, null, PRIVATE), QKF(null, null, null, PRIVATE), QKG(null, null, null, PRIVATE), QKH(
    null, null, null, PRIVATE), QKI(null, null, null, PRIVATE), QKJ(null, null, null, PRIVATE), QKK(
    null, null, null, PRIVATE), QKL(null, null, null, PRIVATE), QKM(null, null, null, PRIVATE), QKN(
    null, null, null, PRIVATE), QKO(null, null, null, PRIVATE), QKP(null, null, null, PRIVATE), QKQ(
    null, null, null, PRIVATE), QKR(null, null, null, PRIVATE), QKS(null, null, null, PRIVATE), QKT(
    null, null, null, PRIVATE), QKU(null, null, null, PRIVATE), QKV(null, null, null, PRIVATE), QKW(
    null, null, null, PRIVATE), QKX(null, null, null, PRIVATE), QKY(null, null, null, PRIVATE), QKZ(
    null, null, null, PRIVATE), QLA(null, null, null, PRIVATE), QLB(null, null, null, PRIVATE), QLC(
    null, null, null, PRIVATE), QLD(null, null, null, PRIVATE), QLE(null, null, null, PRIVATE), QLF(
    null, null, null, PRIVATE), QLG(null, null, null, PRIVATE), QLH(null, null, null, PRIVATE), QLI(
    null, null, null, PRIVATE), QLJ(null, null, null, PRIVATE), QLK(null, null, null, PRIVATE), QLL(
    null, null, null, PRIVATE), QLM(null, null, null, PRIVATE), QLN(null, null, null, PRIVATE), QLO(
    null, null, null, PRIVATE), QLP(null, null, null, PRIVATE), QLQ(null, null, null, PRIVATE), QLR(
    null, null, null, PRIVATE), QLS(null, null, null, PRIVATE), QLT(null, null, null, PRIVATE), QLU(
    null, null, null, PRIVATE), QLV(null, null, null, PRIVATE), QLW(null, null, null, PRIVATE), QLX(
    null, null, null, PRIVATE), QLY(null, null, null, PRIVATE), QLZ(null, null, null, PRIVATE), QMA(
    null, null, null, PRIVATE), QMB(null, null, null, PRIVATE), QMC(null, null, null, PRIVATE), QMD(
    null, null, null, PRIVATE), QME(null, null, null, PRIVATE), QMF(null, null, null, PRIVATE), QMG(
    null, null, null, PRIVATE), QMH(null, null, null, PRIVATE), QMI(null, null, null, PRIVATE), QMJ(
    null, null, null, PRIVATE), QMK(null, null, null, PRIVATE), QML(null, null, null, PRIVATE), QMM(
    null, null, null, PRIVATE), QMN(null, null, null, PRIVATE), QMO(null, null, null, PRIVATE), QMP(
    null, null, null, PRIVATE), QMQ(null, null, null, PRIVATE), QMR(null, null, null, PRIVATE), QMS(
    null, null, null, PRIVATE), QMT(null, null, null, PRIVATE), QMU(null, null, null, PRIVATE), QMV(
    null, null, null, PRIVATE), QMW(null, null, null, PRIVATE), QMX(null, null, null, PRIVATE), QMY(
    null, null, null, PRIVATE), QMZ(null, null, null, PRIVATE), QNA(null, null, null, PRIVATE), QNB(
    null, null, null, PRIVATE), QNC(null, null, null, PRIVATE), QND(null, null, null, PRIVATE), QNE(
    null, null, null, PRIVATE), QNF(null, null, null, PRIVATE), QNG(null, null, null, PRIVATE), QNH(
    null, null, null, PRIVATE), QNI(null, null, null, PRIVATE), QNJ(null, null, null, PRIVATE), QNK(
    null, null, null, PRIVATE), QNL(null, null, null, PRIVATE), QNM(null, null, null, PRIVATE), QNN(
    null, null, null, PRIVATE), QNO(null, null, null, PRIVATE), QNP(null, null, null, PRIVATE), QNQ(
    null, null, null, PRIVATE), QNR(null, null, null, PRIVATE), QNS(null, null, null, PRIVATE), QNT(
    null, null, null, PRIVATE), QNU(null, null, null, PRIVATE), QNV(null, null, null, PRIVATE), QNW(
    null, null, null, PRIVATE), QNX(null, null, null, PRIVATE), QNY(null, null, null, PRIVATE), QNZ(
    null, null, null, PRIVATE), QOA(null, null, null, PRIVATE), QOB(null, null, null, PRIVATE), QOC(
    null, null, null, PRIVATE), QOD(null, null, null, PRIVATE), QOE(null, null, null, PRIVATE), QOF(
    null, null, null, PRIVATE), QOG(null, null, null, PRIVATE), QOH(null, null, null, PRIVATE), QOI(
    null, null, null, PRIVATE), QOJ(null, null, null, PRIVATE), QOK(null, null, null, PRIVATE), QOL(
    null, null, null, PRIVATE), QOM(null, null, null, PRIVATE), QON(null, null, null, PRIVATE), QOO(
    null, null, null, PRIVATE), QOP(null, null, null, PRIVATE), QOQ(null, null, null, PRIVATE), QOR(
    null, null, null, PRIVATE), QOS(null, null, null, PRIVATE), QOT(null, null, null, PRIVATE), QOU(
    null, null, null, PRIVATE), QOV(null, null, null, PRIVATE), QOW(null, null, null, PRIVATE), QOX(
    null, null, null, PRIVATE), QOY(null, null, null, PRIVATE), QOZ(null, null, null, PRIVATE), QPA(
    null, null, null, PRIVATE), QPB(null, null, null, PRIVATE), QPC(null, null, null, PRIVATE), QPD(
    null, null, null, PRIVATE), QPE(null, null, null, PRIVATE), QPF(null, null, null, PRIVATE), QPG(
    null, null, null, PRIVATE), QPH(null, null, null, PRIVATE), QPI(null, null, null, PRIVATE), QPJ(
    null, null, null, PRIVATE), QPK(null, null, null, PRIVATE), QPL(null, null, null, PRIVATE), QPM(
    null, null, null, PRIVATE), QPN(null, null, null, PRIVATE), QPO(null, null, null, PRIVATE), QPP(
    null, null, null, PRIVATE), QPQ(null, null, null, PRIVATE), QPR(null, null, null, PRIVATE), QPS(
    null, null, null, PRIVATE), QPT(null, null, null, PRIVATE), QPU(null, null, null, PRIVATE), QPV(
    null, null, null, PRIVATE), QPW(null, null, null, PRIVATE), QPX(null, null, null, PRIVATE), QPY(
    null, null, null, PRIVATE), QPZ(null, null, null, PRIVATE), QQA(null, null, null, PRIVATE), QQB(
    null, null, null, PRIVATE), QQC(null, null, null, PRIVATE), QQD(null, null, null, PRIVATE), QQE(
    null, null, null, PRIVATE), QQF(null, null, null, PRIVATE), QQG(null, null, null, PRIVATE), QQH(
    null, null, null, PRIVATE), QQI(null, null, null, PRIVATE), QQJ(null, null, null, PRIVATE), QQK(
    null, null, null, PRIVATE), QQL(null, null, null, PRIVATE), QQM(null, null, null, PRIVATE), QQN(
    null, null, null, PRIVATE), QQO(null, null, null, PRIVATE), QQP(null, null, null, PRIVATE), QQQ(
    null, null, null, PRIVATE), QQR(null, null, null, PRIVATE), QQS(null, null, null, PRIVATE), QQT(
    null, null, null, PRIVATE), QQU(null, null, null, PRIVATE), QQV(null, null, null, PRIVATE), QQW(
    null, null, null, PRIVATE), QQX(null, null, null, PRIVATE), QQY(null, null, null, PRIVATE), QQZ(
    null, null, null, PRIVATE), QRA(null, null, null, PRIVATE), QRB(null, null, null, PRIVATE), QRC(
    null, null, null, PRIVATE), QRD(null, null, null, PRIVATE), QRE(null, null, null, PRIVATE), QRF(
    null, null, null, PRIVATE), QRG(null, null, null, PRIVATE), QRH(null, null, null, PRIVATE), QRI(
    null, null, null, PRIVATE), QRJ(null, null, null, PRIVATE), QRK(null, null, null, PRIVATE), QRL(
    null, null, null, PRIVATE), QRM(null, null, null, PRIVATE), QRN(null, null, null, PRIVATE), QRO(
    null, null, null, PRIVATE), QRP(null, null, null, PRIVATE), QRQ(null, null, null, PRIVATE), QRR(
    null, null, null, PRIVATE), QRS(null, null, null, PRIVATE), QRT(null, null, null, PRIVATE), QRU(
    null, null, null, PRIVATE), QRV(null, null, null, PRIVATE), QRW(null, null, null, PRIVATE), QRX(
    null, null, null, PRIVATE), QRY(null, null, null, PRIVATE), QRZ(null, null, null, PRIVATE), QSA(
    null, null, null, PRIVATE), QSB(null, null, null, PRIVATE), QSC(null, null, null, PRIVATE), QSD(
    null, null, null, PRIVATE), QSE(null, null, null, PRIVATE), QSF(null, null, null, PRIVATE), QSG(
    null, null, null, PRIVATE), QSH(null, null, null, PRIVATE), QSI(null, null, null, PRIVATE), QSJ(
    null, null, null, PRIVATE), QSK(null, null, null, PRIVATE), QSL(null, null, null, PRIVATE), QSM(
    null, null, null, PRIVATE), QSN(null, null, null, PRIVATE), QSO(null, null, null, PRIVATE), QSP(
    null, null, null, PRIVATE), QSQ(null, null, null, PRIVATE), QSR(null, null, null, PRIVATE), QSS(
    null, null, null, PRIVATE), QST(null, null, null, PRIVATE), QSU(null, null, null, PRIVATE), QSV(
    null, null, null, PRIVATE), QSW(null, null, null, PRIVATE), QSX(null, null, null, PRIVATE), QSY(
    null, null, null, PRIVATE), QSZ(null, null, null, PRIVATE), QTA(null, null, null, PRIVATE), QTB(
    null, null, null, PRIVATE), QTC(null, null, null, PRIVATE), QTD(null, null, null, PRIVATE), QTE(
    null, null, null, PRIVATE), QTF(null, null, null, PRIVATE), QTG(null, null, null, PRIVATE), QTH(
    null, null, null, PRIVATE), QTI(null, null, null, PRIVATE), QTJ(null, null, null, PRIVATE), QTK(
    null, null, null, PRIVATE), QTL(null, null, null, PRIVATE), QTM(null, null, null, PRIVATE), QTN(
    null, null, null, PRIVATE), QTO(null, null, null, PRIVATE), QTP(null, null, null, PRIVATE), QTQ(
    null, null, null, PRIVATE), QTR(null, null, null, PRIVATE), QTS(null, null, null, PRIVATE), QTT(
    null, null, null, PRIVATE), QTU(null, null, null, PRIVATE), QTV(null, null, null, PRIVATE), QTW(
    null, null, null, PRIVATE), QTX(null, null, null, PRIVATE), QTY(null, null, null, PRIVATE), QTZ(
    null, null, null, PRIVATE),
    RAJ(null, null, null, "Rajasthani"), RAP(null, null, null, "Rapanui"), RAR(null, null, null, "Rarotongan",
    "Cook Islands Maori"), ROA(null, null, null, "Romance (Other)"), ROM(null, null, null, "Romany"), RUP(null,
    null, null, "Aromanian", "Arumanian", "Macedo-Romanian"), SAD(null, null, null, "Sandawe"), SAH(null, null,
    null, "Yakut"), SAI(null, null, null, "South American Indian (Other)"), SAL(null, null, null,
    "Salishan languages"), SAM(null, null, null, "Samaritan Aramaic"), SAS(null, null, null, "Sasak"), SAT(null,
    null, null, "Santali"), SCN(null, null, null, "Sicilian"), SCO(null, null, null, "Scots"), SEL(null, null,
    null, "Selkup"), SEM(null, null, null, "Semitic (Other)"), SGA(null, null, null, "Irish, Old (to 900)"), SGN(
    null, null, null, "Sign Languages"), SHN(null, null, null, "Shan"), SID(null, null, null, "Sidamo"), SIO(null,
    null, null, "Siouan languages"), SIT(null, null, null, "Sino-Tibetan (Other)"), SLA(null, null, null,
    "Slavic (Other)"), SMA(null, null, null, "Southern Sami"), SMI(null, null, null, "Sami languages (Other)"), SMJ(
    null, null, null, "Lule Sami"), SMN(null, null, null, "Inari Sami"), SMS(null, null, null, "Skolt Sami"), SNK(
    null, null, null, "Soninke"), SOG(null, null, null, "Sogdian"), SON(null, null, null, "Songhai languages"), SRN(
    null, null, null, "Sranan Tongo"), SRR(null, null, null, "Serer"), SSA(null, null, null, "Nilo-Saharan (Other)"), SUK(
    null, null, null, "Sukuma"), SUS(null, null, null, "Susu"), SUX(null, null, null, "Sumerian"), SYC(null, null,
    null, "Classical Syriac"), SYR(null, null, null, "Syriac"), TAI(null, null, null, "Tai (Other)"), TEM(null,
    null, null, "Timne"), TER(null, null, null, "Tereno"), TET(null, null, null, "Tetum"), TIG(null, null, null,
    "Tigre"), TIV(null, null, null, "Tiv"), TKL(null, null, null, "Tokelau"), TLH(null, null, null, "Klingon",
    "tlhIngan-Hol"), TLI(null, null, null, "Tlingit"), TMH(null, null, null, "Tamashek"), TOG(null, null, null,
    "Tonga (Nyasa)"), TPI(null, null, null, "Tok Pisin"), TSI(null, null, null, "Tsimshian"), TUM(null, null, null,
    "Tumbuka"), TUP(null, null, null, "Tupi languages"), TUT(null, null, null, "Altaic (Other)"), TVL(null, null,
    null, "Tuvalu"), TYV(null, null, null, "Tuvinian"), UDM(null, null, null, "Udmurt"), UGA(null, null, null,
    "Ugaritic"), UMB(null, null, null, "Umbundu"), UND(null, null, null, "Undetermined"), VAI(null, null, null,
    "Vai"), VOT(null, null, null, "Votic"), WAK(null, null, null, "Wakashan languages"), WAL(null, null, null,
    "Walamo"), WAR(null, null, null, "Waray"), WAS(null, null, null, "Washo"), WEN(null, null, null,
    "Sorbian languages"), XAL(null, null, null, "Kalmyk", "Oirat"), YAO(null, null, null, "Yao"), YAP(null, null,
    null, "Yapese"), YPK(null, null, null, "Yupik languages"), ZAP(null, null, null, "Zapotec"), ZBL(null, null,
    null, "Blissymbols", "Blissymbolics", "Bliss"), ZEN(null, null, null, "Zenaga"), ZND(null, null, null,
    "Zande languages"), ZUN(null, null, null, "Zuni"), ZXX(null, null, null, "No linguistic content"), ZZA(null,
    null, null, "Zaza", "Dimili", "Dimli", "Kirdki", "Kirmanjki", "Zazaki");
    
    private final String deprecated;
    private final String preferred;
    private final String suppressscript;
    private final List<String> descriptions;

    Language(String dep, String pref, String ss, String... desc) {
        this.deprecated = dep;
        this.preferred = pref;
        this.suppressscript = ss;
        this.descriptions = Arrays.asList(desc);
    }

    public static Language valueOf(Subtag subtag) {
        if (subtag != null && subtag.getType() == Subtag.Type.PRIMARY) {
            return valueOf(subtag.getName().toUpperCase(Locale.US));
        }
        throw new IllegalArgumentException("Wrong subtag type");
    }

    public String getDeprecated() {
        return deprecated;
    }

    public boolean isDeprecated() {
        return deprecated != null;
    }

    public String getPreferredValue() {
        return preferred;
    }

    public Language getPreferred() {
        return preferred != null ? valueOf(preferred.toUpperCase(Locale.US)) : this;
    }

    public String getSuppressScript() {
        return suppressscript;
    }

    public String getDescription() {
        return descriptions != null && !descriptions.isEmpty() ? descriptions.get(0) : null;
    }

    public List<String> getDescriptions() {
        return descriptions;
    }

    public Subtag newSubtag() {
        return new Subtag(this);
    }

}
