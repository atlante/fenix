package net.sourceforge.fenixedu.presentationTier.Action.externalServices.epfl;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import net.sourceforge.fenixedu.domain.Person;
import net.sourceforge.fenixedu.domain.Photograph;
import net.sourceforge.fenixedu.domain.PublicCandidacyHashCode;
import net.sourceforge.fenixedu.domain.Qualification;
import net.sourceforge.fenixedu.domain.RootDomainObject;
import net.sourceforge.fenixedu.domain.phd.PhdIndividualProgramProcess;
import net.sourceforge.fenixedu.domain.phd.PhdParticipant;
import net.sourceforge.fenixedu.domain.phd.PhdProgramFocusArea;
import net.sourceforge.fenixedu.domain.phd.PhdProgramProcessDocument;
import net.sourceforge.fenixedu.domain.phd.candidacy.PhdCandidacyPeriod;
import net.sourceforge.fenixedu.domain.phd.candidacy.PhdCandidacyReferee;
import net.sourceforge.fenixedu.domain.phd.candidacy.PhdCandidacyRefereeLetter;
import net.sourceforge.fenixedu.domain.phd.candidacy.PhdProgramPublicCandidacyHashCode;
import net.sourceforge.fenixedu.util.StringUtils;

public class ExportPhdIndividualProgramProcessesInHtml {
    // TODO: IST-<Collaboration>: collaboration must be added as argument
    static final private String APPLICATION_NAME = "Application to the IST-EPFL Joint Doctoral Initiative";
    static final private String APPLICATION_PREFIX_LINK = "/en/about-IST/global-cooperation/IST-EPFL/private/";
    static final private String APPLICANTS_PREFIX_LINK = APPLICATION_PREFIX_LINK + "applicants/";

    static byte[] exportPresentationPage() throws IOException {
	final Page page = new Page();
	page.h2(APPLICATION_NAME);

	for (final Entry<PhdProgramFocusArea, Set<PhdProgramPublicCandidacyHashCode>> entry : getApplicants().entrySet()) {
	    page.h(3, getFocusAreaTitle(entry), "mtop2");

	    page.ulStart();
	    for (final PhdProgramPublicCandidacyHashCode code : entry.getValue()) {
		final String url = "epflCandidateInformation.do?method=displayCandidatePage&amp;candidateOid=" + code.getExternalId();
		page.liStart().link(url, code.getPerson().getName()).liEnd();
	    }
	    page.ulEnd();
	}

	page.close();

	return page.toByteArray();
    }

    private static String getFocusAreaTitle(final Entry<PhdProgramFocusArea, Set<PhdProgramPublicCandidacyHashCode>> entry) {
	return entry.getKey().getName().getContent() + " (" + entry.getValue().size() + " applications)";
    }

    private static String buildPageLinkUrl(final String page) {
	return APPLICATION_PREFIX_LINK + "index.php?page=" + page;
    }

    private static Map<PhdProgramFocusArea, Set<PhdProgramPublicCandidacyHashCode>> getApplicants() {
	final Map<PhdProgramFocusArea, Set<PhdProgramPublicCandidacyHashCode>> candidates = new TreeMap<PhdProgramFocusArea, Set<PhdProgramPublicCandidacyHashCode>>(
		PhdProgramFocusArea.COMPARATOR_BY_NAME);

	PhdCandidacyPeriod mostRecentCandidacyPeriod = PhdCandidacyPeriod.getMostRecentCandidacyPeriod();

	for (final PublicCandidacyHashCode hashCode : RootDomainObject.getInstance().getCandidacyHashCodesSet()) {
	    if (hashCode.isFromPhdProgram() && hashCode.hasCandidacyProcess()) {
		if (!mostRecentCandidacyPeriod.contains(hashCode.getWhenCreated())) {
		    continue;
		}

		final PhdProgramPublicCandidacyHashCode phdHashCode = (PhdProgramPublicCandidacyHashCode) hashCode;
		if (phdHashCode.getPhdProgramCandidacyProcess().isValidatedByCandidate()) {
		    addCandidate(candidates, phdHashCode);
		}
	    }
	}
	return candidates;
    }

    private static void addCandidate(final Map<PhdProgramFocusArea, Set<PhdProgramPublicCandidacyHashCode>> candidates,
	    final PhdProgramPublicCandidacyHashCode hashCode) {

	final PhdProgramFocusArea focusArea = hashCode.getIndividualProgramProcess().getPhdProgramFocusArea();
	if (!candidates.containsKey(focusArea)) {
	    candidates.put(focusArea, new TreeSet<PhdProgramPublicCandidacyHashCode>(
		    new Comparator<PhdProgramPublicCandidacyHashCode>() {

			@Override
			public int compare(PhdProgramPublicCandidacyHashCode o1, PhdProgramPublicCandidacyHashCode o2) {
			    return o1.getPerson().getName().compareTo(o2.getPerson().getName());
			}
		    }));
	}

	candidates.get(focusArea).add(hashCode);
    }

    static byte[] drawCandidatePage(final PhdProgramPublicCandidacyHashCode hashCode) throws IOException {
	final String email = hashCode.getEmail().substring(0, hashCode.getEmail().indexOf("@"));
	final Page page = new Page();

	page.h2(APPLICATION_NAME);
	drawPersonalInformation(page, hashCode, email);
	drawPhdIndividualProgramInformation(page, hashCode);
	drawGuidings(page, hashCode);
	drawQualifications(page, hashCode);
	drawCandidacyReferees(page, hashCode, email);
	drawDocuments(page, hashCode, email);

	page.close();
	return page.toByteArray();
    }

    private static void drawPersonalInformation(final Page page, final PhdProgramPublicCandidacyHashCode hashCode,
	    final String folderName) throws IOException {

	final Person person = hashCode.getPerson();

	page.h(3, "Personal Information", "mtop2");
	page.tableStart("tstyle2 thwhite thnowrap thlight thleft thtop ulnomargin width100pc");

	page.rowStart("tdbold").headerStartWithStyle("width: 125px;").write("Name:").headerEnd().column(person.getName())
		.rowEnd();
	page.rowStart().header("Gender:").column(person.getGender().toLocalizedString(Locale.ENGLISH)).rowEnd();
	page.rowStart().header("Identity card type:").column(person.getIdDocumentType().getLocalizedName()).rowEnd();
	page.rowStart().header("Identity card #:").column(person.getDocumentIdNumber()).rowEnd();
	page.rowStart().header("Issued by:").column(person.getEmissionLocationOfDocumentId()).rowEnd();
	page.rowStart().header("Fiscal number:").column(string(person.getSocialSecurityNumber())).rowEnd();
	page.rowStart().header("Date of birth:").column(person.getDateOfBirthYearMonthDay().toString("dd/MM/yyyy")).rowEnd();
	page.rowStart().header("Birthplace:").column(person.getDistrictSubdivisionOfBirth()).rowEnd();
	page.rowStart().header("Nationality:").column(person.getCountry().getCountryNationality().getContent()).rowEnd();
	page.rowStart().header("Address:").column(person.getAddress()).rowEnd();
	page.rowStart().header("City:").column(person.getArea()).rowEnd();
	page.rowStart().header("Zip code:").column(person.getAreaCode()).rowEnd();
	page.rowStart().header("Country:").column(
		(person.getCountryOfResidence() != null ? person.getCountryOfResidence().getName() : "-")).rowEnd();
	page.rowStart().header("Phone:").column(person.getDefaultPhoneNumber()).rowEnd();
	page.rowStart().header("Mobile:").column(person.getDefaultMobilePhoneNumber()).rowEnd();
	page.rowStart().header("Email:").column(person.getDefaultEmailAddressValue()).rowEnd();

	page.tableEnd();

	page.h(3, "Photo");
	String photoUrl = "epflCandidateInformation.do?method=displayPhoto";
	final Photograph photo = person.getPersonalPhotoEvenIfPending();
	if (photo != null) {
	    photoUrl += "&amp;photoOid=" + photo.getExternalId();
	}
	page.photo(photoUrl);
    }

    private static void drawPhdIndividualProgramInformation(final Page page, final PhdProgramPublicCandidacyHashCode hashCode)
	    throws IOException {
	final PhdIndividualProgramProcess process = hashCode.getIndividualProgramProcess();

	page.h(3, "Application information");
	page.tableStart("tstyle2 thwhite thnowrap thlight thleft thtop ulnomargin width100pc");
	page.rowStart().headerStartWithStyle("width: 125px;").write("Candidacy Date:").headerEnd().column(
		process.getCandidacyDate().toString("dd/MM/yyyy")).rowEnd();
	page.rowStart().header("Area:").column(process.getPhdProgramFocusArea().getName().getContent()).rowEnd();
	page.rowStart().header("Title:").column(string(process.getThesisTitle())).rowEnd();
	page.rowStart().header("Collaboration:").column(process.getCollaborationTypeName()).rowEnd();
	page.rowStart().header("Year:").column(process.getExecutionYear().getYear()).rowEnd();
	page.tableEnd();
    }

    private static void drawDocuments(final Page page, final PhdProgramPublicCandidacyHashCode hashCode, final String folderName) throws IOException {

	page.h(3, "Documents", "mtop2");

	final PhdIndividualProgramProcess process = hashCode.getIndividualProgramProcess();
	if (!process.getCandidacyProcessDocuments().isEmpty()) {

	    final String documentName = folderName + "-documents.zip";
	    final String url = "epflCandidateInformation.do?method=downloadCandidateDocuments&amp;candidateOid=" + hashCode.getExternalId();
	    page.pStart("mbottom0").link(url, documentName).pEnd();

	    page.tableStart("tstyle2 thwhite thnowrap thlight thleft thtop ulnomargin width100pc");
	    page.rowStart().header("Document type").header("Upload time").header("Filename").rowEnd();

	    for (final PhdProgramProcessDocument document : process.getCandidacyProcessDocuments()) {
		page.rowStart().column(document.getDocumentType().getLocalizedName());
		page.column(document.getUploadTime().toString("dd/MM/yyyy HH:mm"));
		page.column(document.getFilename()).rowEnd();
	    }

	    page.tableEnd();
	}
    }

    static byte[] createZip(final PhdProgramPublicCandidacyHashCode hashCode) {

	final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	ZipOutputStream zip = null;
	try {
	    zip = new ZipOutputStream(outputStream);

	    int count = 1;
	    for (final PhdProgramProcessDocument document : hashCode.getIndividualProgramProcess().getCandidacyProcessDocuments()) {
		final ZipEntry zipEntry = new ZipEntry(count + "-" + document.getFilename());
		zip.putNextEntry(zipEntry);

		// TODO: use in local context copy(new ByteArrayInputStream(new
		// byte[20]), zip);
		copy(document.getStream(), zip);

		zip.closeEntry();
		count++;
	    }
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	} finally {
	    if (zip != null) {
		try {
		    zip.flush();
		    zip.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	}
	return outputStream.toByteArray();
    }

    private static void drawCandidacyReferees(final Page page, final PhdProgramPublicCandidacyHashCode hashCode,
	    final String folderName) throws IOException {
	final PhdIndividualProgramProcess process = hashCode.getIndividualProgramProcess();
	page.h(3, "Reference letters (referees)", "mtop2");

	if (!process.getPhdCandidacyReferees().isEmpty()) {
	    int count = 1;
	    for (final PhdCandidacyReferee referee : process.getPhdCandidacyReferees()) {
		page.pStart("mbottom0").strong(String.valueOf(count) + ". ").pEnd();
		drawReferee(page, referee, count, folderName);
		count++;
	    }
	} else {
	    page.pStart().write("Not defined").pEnd();
	}
    }

    private static void drawReferee(final Page page, final PhdCandidacyReferee referee, final int count, final String folderName)
    		throws IOException {

	page.tableStart("tstyle2 thwhite thnowrap thlight thleft thtop ulnomargin width100pc");
	page.rowStart().headerStartWithStyle("width: 125px;").write("Name:").headerEnd().column(referee.getName()).rowEnd();
	page.rowStart().header("Email:").column(referee.getEmail()).rowEnd();
	page.rowStart().header("Institution:").column(referee.getInstitution()).rowEnd();

	if (referee.isLetterAvailable()) {
	    page.rowStart().header("Referee form submitted:");
	    final String url = "epflCandidateInformation.do?method=displayRefereePage&amp;refereeOid="
			+ referee.getExternalId()
			+ "&amp;count="
			+ count;
	    page.columnStart().link(url, "Yes").columnEnd().rowEnd();
	} else {
	    page.rowStart().header("Referee form submitted:").column("No").rowEnd();
	}

	page.tableEnd();
    }

    static byte[] drawLetter(final PhdCandidacyReferee referee, final int count) throws IOException {
	final Page page = new Page();

	page.h2(APPLICATION_NAME);
	page.h(3, "Applicant", "mtop2");

	candidateInformation(referee, page);
	letterInformation(referee, page);

	page.close();
	return page.toByteArray();
    }

    private static void letterInformation(final PhdCandidacyReferee referee, final Page page) throws IOException {

	final PhdCandidacyRefereeLetter letter = referee.getLetter();

	page.h(3, "Reference Letter", "mtop2");
	page.tableStart("tstyle2 thwhite thnowrap thlight thleft thtop ulnomargin width100pc");

	page.rowStart().headerStartWithStyle("width: 200px;").write("How long have you known the applicant?").headerEnd().column(
		string(letter.getHowLongKnownApplicant()) + " months").rowEnd();
	page.rowStart().header("In what capacity?").column(string(letter.getCapacity())).rowEnd();
	page.rowStart().header("Comparison group:").column(string(letter.getComparisonGroup())).rowEnd();
	page.rowStart().header("Rank in class (if applicable):").column(string(letter.getRankInClass())).rowEnd();
	page.rowStart().header("Academic performance:").column(string(letter.getAcademicPerformance().getLocalizedName()))
		.rowEnd();
	page.rowStart().header("Social and Communication Skills:").column(
		string(letter.getSocialAndCommunicationSkills().getLocalizedName())).rowEnd();
	page.rowStart().header("Potential to excel in a PhD:").column(string(letter.getPotencialToExcelPhd().getLocalizedName()))
		.rowEnd();

	page.rowStart().header("Recomendation letter:");
	if (letter.hasFile()) {
	    page.column(letter.getFile().getDisplayName() + " (file is inside documents zip file)");
	} else {
	    page.column("-");
	}
	page.rowEnd();

	page.rowStart().header("Comments:").column(string(letter.getComments())).rowEnd();
	page.rowStart().header("Name:").column(string(letter.getRefereeName())).rowEnd();
	page.rowStart().header("Position/Title:").column(string(letter.getRefereePosition())).rowEnd();
	page.rowStart().header("Institution:").column(string(letter.getRefereeInstitution())).rowEnd();
	page.rowStart().header("Address:").column(string(letter.getRefereeInstitution())).rowEnd();
	page.rowStart().header("City:").column(string(letter.getRefereeCity())).rowEnd();
	page.rowStart().header("Zip code:").column(string(letter.getRefereeZipCode())).rowEnd();
	page.rowStart().header("Country:").column(
		letter.getRefereeCountry() != null ? letter.getRefereeCountry().getLocalizedName().getContent() : "-").rowEnd();
	page.rowStart().header("Email:").column(string(letter.getRefereeEmail())).rowEnd();

	page.tableEnd();
    }

    private static void candidateInformation(final PhdCandidacyReferee referee, final Page page) throws IOException {
	page.tableStart("tstyle2 thwhite thnowrap thlight thleft thtop ulnomargin width100pc");
	page.rowStart("tdbold").headerStartWithStyle("width: 200px;").write("Name: ").headerEnd().column(
		referee.getPhdProgramCandidacyProcess().getPerson().getName()).rowEnd();
	page.tableEnd();
    }

    private static void drawQualifications(final Page page, final PhdProgramPublicCandidacyHashCode hashCode) throws IOException {
	final PhdIndividualProgramProcess process = hashCode.getIndividualProgramProcess();
	page.h(3, "Academic Degrees", "mtop2");

	if (!process.getQualifications().isEmpty()) {
	    int count = 1;
	    for (final Qualification qualification : process.getQualifications()) {
		page.pStart("mbottom0").strong(String.valueOf(count) + ". ").pEnd();
		drawQualification(page, qualification);
		count++;
	    }
	} else {
	    page.pStart().write("Not defined").pEnd();
	}
    }

    private static void drawQualification(final Page page, final Qualification qualification) throws IOException {
	page.tableStart("tstyle2 thwhite thnowrap thlight thleft thtop ulnomargin width100pc");
	page.rowStart().header("Type:").column(qualification.getType().getLocalizedName()).rowEnd();
	page.rowStart().header("Scientific Field:").column(qualification.getDegree()).rowEnd();
	page.rowStart().header("Institution:").column(qualification.getSchool()).rowEnd();
	page.rowStart().header("Grade:").column(qualification.getMark()).rowEnd();
	page.rowStart().header("Attended from:").column(
		(qualification.getAttendedBegin() != null ? qualification.getAttendedBegin().toString("MM/yyyy") : "-")).rowEnd();
	page.rowStart().header("Attended to:").column(
		(qualification.getAttendedEnd() != null ? qualification.getAttendedEnd().toString("MM/yyyy") : "-")).rowEnd();
	page.tableEnd();
    }

    private static void drawGuidings(final Page page, final PhdProgramPublicCandidacyHashCode hashCode) throws IOException {
	final PhdIndividualProgramProcess process = hashCode.getIndividualProgramProcess();
	page.h(3, "Phd supervisors (if applicable)", "mtop2");

	if (process.hasAnyGuidings()) {
	    int count = 1;
	    for (final PhdParticipant guiding : process.getGuidings()) {
		page.pStart("mbottom0").strong(String.valueOf(count) + ". ").pEnd();
		drawGuiding(page, guiding);
		count++;
	    }
	} else {
	    page.pStart().write("Not defined").pEnd();
	}
    }

    private static void drawGuiding(final Page page, final PhdParticipant guiding) throws IOException {
	page.tableStart("tstyle2 thwhite thnowrap thlight thleft thtop ulnomargin width100pc");
	page.rowStart().headerStartWithStyle("width: 125px;").write("Name:").headerEnd().column(guiding.getName()).rowEnd();
	page.rowStart().header("Affiliation:").column(guiding.getWorkLocation()).rowEnd();
	page.rowStart().header("Email:").column(guiding.getEmail()).rowEnd();
	page.tableEnd();
    }

    private static String string(final String value) {
	return (value != null) ? value : StringUtils.EMPTY;
    }

    /*
     * do not close output stream
     */
    private static void copy(final InputStream inputStream, final OutputStream outputStream) throws IOException {
	int BUFFER_SIZE = 1024 * 1024;
	try {
	    final byte[] buffer = new byte[BUFFER_SIZE];
	    for (int numberOfBytesRead; (numberOfBytesRead = inputStream.read(buffer, 0, BUFFER_SIZE)) != -1; outputStream.write(
		    buffer, 0, numberOfBytesRead))
		;
	} finally {
	    inputStream.close();
	}

    }

    static private class Page {
	private ByteArrayOutputStream writer = new ByteArrayOutputStream();

	Page() throws IOException {
	    write("<xhtml>");
	    write("<head>");
	    write("</head>");
	    write("<body>");
	}

	public byte[] toByteArray() throws IOException {
	    write("</body>");
	    write("</xhtml>");
	    return writer.toByteArray();
	}

	public Page h2(final String body) throws IOException {
	    return h(2, body);
	}

	public Page h(final int level, final String body) throws IOException {
	    return startTag("h" + level).write(body).endTag("h" + level);
	}

	public Page h(final int level, final String body, final String classes) throws IOException {
	    return write(String.format("<h%s class=\"%s\">", level, classes)).write(body).endTag("h" + level);
	}

	public Page link(final String path, final String name) throws IOException {
	    return write(String.format("<a href='%s'>%s</a>", path, name));
	}

	public Page strong(final String body) throws IOException {
	    return startTag("strong").write(body).endTag("strong");
	}

	public Page tableStart(String classes) throws IOException {
	    return write(String.format("<table class=\"%s\">", classes));
	}

	public Page rowStart() throws IOException {
	    return startTag("tr");
	}

	public Page rowStart(final String classes) throws IOException {
	    return write(String.format("<tr class=\"%s\"", classes));
	}

	public Page rowEnd() throws IOException {
	    return endTag("tr");
	}

	public Page headerStartWithStyle(final String style) throws IOException {
	    return write(String.format("<th style=\"%s\">", style));
	}

	public Page headerEnd() throws IOException {
	    return endTag("th");
	}

	public Page header(final String body) throws IOException {
	    return write(String.format("<th>%s</th>", body));
	}

	public Page columnStart() throws IOException {
	    return startTag("td");
	}

	public Page columnEnd() throws IOException {
	    return endTag("td");
	}

	public Page column(final String body) throws IOException {
	    return write(String.format("<td>%s</td>", body));
	}

	public Page tableEnd() throws IOException {
	    return endTag("table");
	}

	public Page ulStart() throws IOException {
	    return startTag("ul");
	}

	public Page ulEnd() throws IOException {
	    return endTag("ul");
	}

	public Page liStart() throws IOException {
	    return startTag("li");
	}

	public Page liEnd() throws IOException {
	    return endTag("li");
	}

	public Page pStart(final String classes) throws IOException {
	    return write(String.format("<p class=\"%s\">", classes));
	}

	public Page pStart() throws IOException {
	    return startTag("p");
	}

	public Page pEnd() throws IOException {
	    return endTag("p");
	}

	public Page photo(final String photoPath) throws IOException {
	    return write(String.format("<img src=\"%s\" />", photoPath));
	}

	private Page startTag(final String tagName) throws IOException {
	    return write("<" + tagName + ">");
	}

	private Page endTag(final String tagName) throws IOException {
	    return write("</" + tagName + ">");
	}

	public Page write(final String value) throws IOException {
	    writer.write(value.getBytes("ISO8859-1"));
	    writer.write("\n".getBytes());
	    return this;
	}

	public void close() throws IOException {
	    if (writer != null) {
		writer.flush();
		writer.close();
	    }
	}
    }

}