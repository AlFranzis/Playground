package al.franzis.lucene.header.serversource;

import org.dcm4che2.data.UID;

public class Constants {

	public static final String[] IVRLE_TS = {
        UID.ImplicitVRLittleEndian };

	public static final String[] NATIVE_LE_TS = {
        UID.ExplicitVRLittleEndian,
        UID.ImplicitVRLittleEndian};

	public static final String[] NATIVE_BE_TS = {
        UID.ExplicitVRBigEndian,
        UID.ImplicitVRLittleEndian};

	public static final String[] DEFLATED_TS = {
        UID.DeflatedExplicitVRLittleEndian,
        UID.ExplicitVRLittleEndian,
        UID.ImplicitVRLittleEndian};

	public static final String[] NOPX_TS = {
        UID.NoPixelData,
        UID.ExplicitVRLittleEndian,
        UID.ImplicitVRLittleEndian};

	public static final String[] NOPXDEFL_TS = {
        UID.NoPixelDataDeflate,
        UID.NoPixelData,
        UID.ExplicitVRLittleEndian,
        UID.ImplicitVRLittleEndian};

	public static final String[] JPLL_TS = {
        UID.JPEGLossless,
        UID.JPEGLosslessNonHierarchical14,
        UID.JPEGLSLossless,
        UID.JPEG2000LosslessOnly,
        UID.ExplicitVRLittleEndian,
        UID.ImplicitVRLittleEndian};

	public static final String[] JPLY_TS = {
        UID.JPEGBaseline1,
        UID.JPEGExtended24,
        UID.JPEGLSLossyNearLossless,
        UID.JPEG2000,
        UID.ExplicitVRLittleEndian,
        UID.ImplicitVRLittleEndian};

	public static final String[] MPEG2_TS = { UID.MPEG2 };

	
	public static final String[] DEF_TS = {
        UID.JPEGLossless,
        UID.JPEGLosslessNonHierarchical14,
        UID.JPEGLSLossless,
        UID.JPEGLSLossyNearLossless,
        UID.JPEG2000LosslessOnly,
        UID.JPEG2000,
        UID.JPEGBaseline1,
        UID.JPEGExtended24,
        UID.MPEG2,
        UID.DeflatedExplicitVRLittleEndian,
        UID.ExplicitVRBigEndian,
        UID.ExplicitVRLittleEndian,
        UID.ImplicitVRLittleEndian};
	
	public static enum TS {
        IVLE(IVRLE_TS),
        LE(NATIVE_LE_TS),
        BE(NATIVE_BE_TS),
        DEFL(DEFLATED_TS),
        JPLL(JPLL_TS),
        JPLY(JPLY_TS),
        MPEG2(MPEG2_TS),
        NOPX(NOPX_TS),
        NOPXD(NOPXDEFL_TS);
        
        final String[] uids;
        TS(String[] uids) { this.uids = uids; }
    }
	
	 public static enum CUID {
	        CR(UID.ComputedRadiographyImageStorage),
	        CT(UID.CTImageStorage),
	        MR(UID.MRImageStorage),
	        US(UID.UltrasoundImageStorage),
	        NM(UID.NuclearMedicineImageStorage),
	        PET(UID.PositronEmissionTomographyImageStorage),
	        SC(UID.SecondaryCaptureImageStorage),
	        XA(UID.XRayAngiographicImageStorage),
	        XRF(UID.XRayRadiofluoroscopicImageStorage),
	        DX(UID.DigitalXRayImageStorageForPresentation),
	        MG(UID.DigitalMammographyXRayImageStorageForPresentation),
	        PR(UID.GrayscaleSoftcopyPresentationStateStorageSOPClass),
	        KO(UID.KeyObjectSelectionDocumentStorage),
	        SR(UID.BasicTextSRStorage);

	        public final String uid;
	        CUID(String uid) { this.uid = uid; }
 
	    }
	 
}
