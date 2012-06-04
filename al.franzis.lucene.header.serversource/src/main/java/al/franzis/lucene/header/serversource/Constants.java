package al.franzis.lucene.header.serversource;

import org.dcm4che2.data.Tag;
import org.dcm4che2.data.UID;

public class Constants {

	public static enum QueryRetrieveLevel {
		PATIENT("PATIENT", PATIENT_RETURN_KEYS, PATIENT_LEVEL_FIND_CUID,
				PATIENT_LEVEL_GET_CUID, PATIENT_LEVEL_MOVE_CUID), STUDY(
				"STUDY", STUDY_RETURN_KEYS, STUDY_LEVEL_FIND_CUID,
				STUDY_LEVEL_GET_CUID, STUDY_LEVEL_MOVE_CUID), SERIES("SERIES",
				SERIES_RETURN_KEYS, SERIES_LEVEL_FIND_CUID,
				SERIES_LEVEL_GET_CUID, SERIES_LEVEL_MOVE_CUID), IMAGE("IMAGE",
				INSTANCE_RETURN_KEYS, SERIES_LEVEL_FIND_CUID,
				SERIES_LEVEL_GET_CUID, SERIES_LEVEL_MOVE_CUID);

		private final String code;
		private final int[] returnKeys;
		private final String[] findClassUids;
		private final String[] getClassUids;
		private final String[] moveClassUids;

		private QueryRetrieveLevel(String code, int[] returnKeys,
				String[] findClassUids, String[] getClassUids,
				String[] moveClassUids) {
			this.code = code;
			this.returnKeys = returnKeys;
			this.findClassUids = findClassUids;
			this.getClassUids = getClassUids;
			this.moveClassUids = moveClassUids;
		}

		public String getCode() {
			return code;
		}

		public int[] getReturnKeys() {
			return returnKeys;
		}

		public String[] getFindClassUids() {
			return findClassUids;
		}

		public String[] getGetClassUids() {
			return getClassUids;
		}

		public String[] getMoveClassUids() {
			return moveClassUids;
		}
	}

	public static final String[] PATIENT_LEVEL_FIND_CUID = {
			UID.PatientRootQueryRetrieveInformationModelFIND,
			UID.PatientStudyOnlyQueryRetrieveInformationModelFINDRetired };

	public static final String[] STUDY_LEVEL_FIND_CUID = {
			UID.StudyRootQueryRetrieveInformationModelFIND,
			UID.PatientRootQueryRetrieveInformationModelFIND,
			UID.PatientStudyOnlyQueryRetrieveInformationModelFINDRetired };

	public static final String[] SERIES_LEVEL_FIND_CUID = {
			UID.StudyRootQueryRetrieveInformationModelFIND,
			UID.PatientRootQueryRetrieveInformationModelFIND, };

	public static final String[] PATIENT_LEVEL_GET_CUID = {
			UID.PatientRootQueryRetrieveInformationModelGET,
			UID.PatientStudyOnlyQueryRetrieveInformationModelGETRetired };

	public static final String[] STUDY_LEVEL_GET_CUID = {
			UID.StudyRootQueryRetrieveInformationModelGET,
			UID.PatientRootQueryRetrieveInformationModelGET,
			UID.PatientStudyOnlyQueryRetrieveInformationModelGETRetired };

	public static final String[] SERIES_LEVEL_GET_CUID = {
			UID.StudyRootQueryRetrieveInformationModelGET,
			UID.PatientRootQueryRetrieveInformationModelGET };

	public static final String[] PATIENT_LEVEL_MOVE_CUID = {
			UID.PatientRootQueryRetrieveInformationModelMOVE,
			UID.PatientStudyOnlyQueryRetrieveInformationModelMOVERetired };

	public static final String[] STUDY_LEVEL_MOVE_CUID = {
			UID.StudyRootQueryRetrieveInformationModelMOVE,
			UID.PatientRootQueryRetrieveInformationModelMOVE,
			UID.PatientStudyOnlyQueryRetrieveInformationModelMOVERetired };

	public static final String[] SERIES_LEVEL_MOVE_CUID = {
			UID.StudyRootQueryRetrieveInformationModelMOVE,
			UID.PatientRootQueryRetrieveInformationModelMOVE };

	public static final int[] PATIENT_RETURN_KEYS = { Tag.PatientName,
			Tag.PatientID, Tag.PatientBirthDate, Tag.PatientSex,
			Tag.NumberOfPatientRelatedStudies,
			Tag.NumberOfPatientRelatedSeries,
			Tag.NumberOfPatientRelatedInstances };

	public static final int[] PATIENT_MATCHING_KEYS = { Tag.PatientName,
			Tag.PatientID, Tag.IssuerOfPatientID, Tag.PatientBirthDate,
			Tag.PatientSex };

	public static final int[] STUDY_RETURN_KEYS = { Tag.StudyDate,
			Tag.StudyTime, Tag.AccessionNumber, Tag.StudyID,
			Tag.StudyInstanceUID, Tag.NumberOfStudyRelatedSeries,
			Tag.NumberOfStudyRelatedInstances };

	public static final int[] STUDY_MATCHING_KEYS = { Tag.StudyDate,
			Tag.StudyTime, Tag.AccessionNumber, Tag.ModalitiesInStudy,
			Tag.ReferringPhysicianName, Tag.StudyID, Tag.StudyInstanceUID };

	public static final int[] PATIENT_STUDY_MATCHING_KEYS = { Tag.StudyDate,
			Tag.StudyTime, Tag.AccessionNumber, Tag.ModalitiesInStudy,
			Tag.ReferringPhysicianName, Tag.PatientName, Tag.PatientID,
			Tag.IssuerOfPatientID, Tag.PatientBirthDate, Tag.PatientSex,
			Tag.StudyID, Tag.StudyInstanceUID };

	public static final int[] SERIES_RETURN_KEYS = { Tag.Modality,
			Tag.SeriesNumber, Tag.SeriesInstanceUID,
			Tag.NumberOfSeriesRelatedInstances };

	public static final int[] SERIES_MATCHING_KEYS = { Tag.Modality,
			Tag.SeriesNumber, Tag.SeriesInstanceUID,
			Tag.RequestAttributesSequence };

	public static final int[] INSTANCE_RETURN_KEYS = { Tag.InstanceNumber,
			Tag.SOPClassUID, Tag.SOPInstanceUID, };

	public static final int[] MOVE_KEYS = { Tag.QueryRetrieveLevel,
			Tag.PatientID, Tag.StudyInstanceUID, Tag.SeriesInstanceUID,
			Tag.SOPInstanceUID, };

	public static final String[] IVRLE_TS = { UID.ImplicitVRLittleEndian };

	public static final String[] NATIVE_LE_TS = { UID.ExplicitVRLittleEndian,
			UID.ImplicitVRLittleEndian };

	public static final String[] NATIVE_BE_TS = { UID.ExplicitVRBigEndian,
			UID.ImplicitVRLittleEndian };

	public static final String[] DEFLATED_TS = {
			UID.DeflatedExplicitVRLittleEndian, UID.ExplicitVRLittleEndian,
			UID.ImplicitVRLittleEndian };

	public static final String[] NOPX_TS = { UID.NoPixelData,
			UID.ExplicitVRLittleEndian, UID.ImplicitVRLittleEndian };

	public static final String[] NOPXDEFL_TS = { UID.NoPixelDataDeflate,
			UID.NoPixelData, UID.ExplicitVRLittleEndian,
			UID.ImplicitVRLittleEndian };

	public static final String[] JPLL_TS = { UID.JPEGLossless,
			UID.JPEGLosslessNonHierarchical14, UID.JPEGLSLossless,
			UID.JPEG2000LosslessOnly, UID.ExplicitVRLittleEndian,
			UID.ImplicitVRLittleEndian };

	public static final String[] JPLY_TS = { UID.JPEGBaseline1,
			UID.JPEGExtended24, UID.JPEGLSLossyNearLossless, UID.JPEG2000,
			UID.ExplicitVRLittleEndian, UID.ImplicitVRLittleEndian };

	public static final String[] MPEG2_TS = { UID.MPEG2 };

	public static final String[] DEF_TS = { UID.JPEGLossless,
			UID.JPEGLosslessNonHierarchical14, UID.JPEGLSLossless,
			UID.JPEGLSLossyNearLossless, UID.JPEG2000LosslessOnly,
			UID.JPEG2000, UID.JPEGBaseline1, UID.JPEGExtended24, UID.MPEG2,
			UID.DeflatedExplicitVRLittleEndian, UID.ExplicitVRBigEndian,
			UID.ExplicitVRLittleEndian, UID.ImplicitVRLittleEndian };

	public static enum TS {
		IVLE(IVRLE_TS), LE(NATIVE_LE_TS), BE(NATIVE_BE_TS), DEFL(DEFLATED_TS), JPLL(
				JPLL_TS), JPLY(JPLY_TS), MPEG2(MPEG2_TS), NOPX(NOPX_TS), NOPXD(
				NOPXDEFL_TS);

		final String[] uids;

		TS(String[] uids) {
			this.uids = uids;
		}
	}

	public static enum CUID {
		CR(UID.ComputedRadiographyImageStorage), CT(UID.CTImageStorage), MR(
				UID.MRImageStorage), US(UID.UltrasoundImageStorage), NM(
				UID.NuclearMedicineImageStorage), PET(
				UID.PositronEmissionTomographyImageStorage), SC(
				UID.SecondaryCaptureImageStorage), XA(
				UID.XRayAngiographicImageStorage), XRF(
				UID.XRayRadiofluoroscopicImageStorage), DX(
				UID.DigitalXRayImageStorageForPresentation), MG(
				UID.DigitalMammographyXRayImageStorageForPresentation), PR(
				UID.GrayscaleSoftcopyPresentationStateStorageSOPClass), KO(
				UID.KeyObjectSelectionDocumentStorage), SR(
				UID.BasicTextSRStorage);

		public final String uid;

		CUID(String uid) {
			this.uid = uid;
		}

	}

}
