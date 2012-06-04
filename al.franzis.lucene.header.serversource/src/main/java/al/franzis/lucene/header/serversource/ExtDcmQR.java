/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is part of dcm4che, an implementation of DICOM(TM) in
 * Java(TM), hosted at http://sourceforge.net/projects/dcm4che.
 *
 * The Initial Developer of the Original Code is
 * Gunter Zeilinger, Huetteldorferstr. 24/10, 1150 Vienna/Austria/Europe.
 * Portions created by the Initial Developer are Copyright (C) 2002-2005
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 * Gunter Zeilinger <gunterze@gmail.com>
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */

package al.franzis.lucene.header.serversource;

import static al.franzis.lucene.header.serversource.Constants.MOVE_KEYS;
import static al.franzis.lucene.header.serversource.Constants.PATIENT_MATCHING_KEYS;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

import org.dcm4che2.data.BasicDicomObject;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.UID;
import org.dcm4che2.data.UIDDictionary;
import org.dcm4che2.data.VR;
import org.dcm4che2.net.Association;
import org.dcm4che2.net.CommandUtils;
import org.dcm4che2.net.ConfigurationException;
import org.dcm4che2.net.Device;
import org.dcm4che2.net.DimseRSP;
import org.dcm4che2.net.DimseRSPHandler;
import org.dcm4che2.net.ExtQueryTransferCapability;
import org.dcm4che2.net.ExtRetrieveTransferCapability;
import org.dcm4che2.net.NetworkApplicationEntity;
import org.dcm4che2.net.NetworkConnection;
import org.dcm4che2.net.NewThreadExecutor;
import org.dcm4che2.net.NoPresentationContextException;
import org.dcm4che2.net.TransferCapability;
import org.dcm4che2.net.UserIdentity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import al.franzis.lucene.header.serversource.Constants.QueryRetrieveLevel;


public class ExtDcmQR {
    private static final Logger LOG = LoggerFactory.getLogger(ExtDcmQR.class);

    private static char[] SECRET = { 's', 'e', 'c', 'r', 'e', 't' };

    private static final String[] EMPTY_STRING = {};

    private final Executor executor;

    private final NetworkApplicationEntity remoteAE = new NetworkApplicationEntity();

    private final NetworkConnection remoteConn = new NetworkConnection();

    private final Device device;

    private final NetworkApplicationEntity ae = new NetworkApplicationEntity();

    private final NetworkConnection conn = new NetworkConnection();

    private Association assoc;

    private int priority = 0;

    private boolean cfind;

    private boolean cget; 

    private String moveDest;

    private boolean evalRetrieveAET = false;

    private Constants.QueryRetrieveLevel qrlevel = Constants.QueryRetrieveLevel.STUDY;

    private List<String> privateFind = new ArrayList<String>();

    private final List<TransferCapability> storeTransferCapability =
            new ArrayList<TransferCapability>(8);

    private DicomObject keys = new BasicDicomObject();

    private int cancelAfter = Integer.MAX_VALUE;

    private int completed;

    private int warning;

    private int failed;

    private boolean relationQR;

    private boolean dateTimeMatching;

    private boolean fuzzySemanticPersonNameMatching;

    private boolean noExtNegotiation;

    private String keyStoreURL = "resource:tls/test_sys_1.p12";
    
    private char[] keyStorePassword = SECRET; 

    private char[] keyPassword; 
    
    private String trustStoreURL = "resource:tls/mesa_certs.jks";
    
    private char[] trustStorePassword = SECRET;
    
    public ExtDcmQR(String name) {
        device = new Device(name);
        executor = new NewThreadExecutor(name);
        remoteAE.setInstalled(true);
        remoteAE.setAssociationAcceptor(true);
        remoteAE.setNetworkConnection(new NetworkConnection[] { remoteConn });

        device.setNetworkApplicationEntity(ae);
        device.setNetworkConnection(conn);
        ae.setNetworkConnection(conn);
        ae.setAssociationInitiator(true);
        ae.setAssociationAcceptor(true);
        ae.setAETitle(name);
    }

    public final void setLocalHost(String hostname) {
        conn.setHostname(hostname);
    }

    public final void setLocalPort(int port) {
        conn.setPort(port);
    }

    public final void setRemoteHost(String hostname) {
        remoteConn.setHostname(hostname);
    }

    public final void setRemotePort(int port) {
        remoteConn.setPort(port);
    }

    public final void setTlsProtocol(String[] tlsProtocol) {
        conn.setTlsProtocol(tlsProtocol);
    }

    public final void setTlsWithoutEncyrption() {
        conn.setTlsWithoutEncyrption();
        remoteConn.setTlsWithoutEncyrption();
    }

    public final void setTls3DES_EDE_CBC() {
        conn.setTls3DES_EDE_CBC();
        remoteConn.setTls3DES_EDE_CBC();
    }

    public final void setTlsAES_128_CBC() {
        conn.setTlsAES_128_CBC();
        remoteConn.setTlsAES_128_CBC();
    }

    public final void setTlsNeedClientAuth(boolean needClientAuth) {
        conn.setTlsNeedClientAuth(needClientAuth);
    }

    public final void setKeyStoreURL(String url) {
        keyStoreURL = url;
    }
    
    public final void setKeyStorePassword(String pw) {
        keyStorePassword = pw.toCharArray();
    }
    
    public final void setKeyPassword(String pw) {
        keyPassword = pw.toCharArray();
    }
    
    public final void setTrustStorePassword(String pw) {
        trustStorePassword = pw.toCharArray();
    }
    
    public final void setTrustStoreURL(String url) {
        trustStoreURL = url;
    }

    public final void setCalledAET(String called, boolean reuse) {
        remoteAE.setAETitle(called);
        if (reuse)
            ae.setReuseAssocationToAETitle(new String[] { called });
    }

    public final void setCalling(String calling) {
        ae.setAETitle(calling);
    }

    public final void setUserIdentity(UserIdentity userIdentity) {
        ae.setUserIdentity(userIdentity);
    }

    public final void setPriority(int priority) {
        this.priority = priority;
    }

    public final void setConnectTimeout(int connectTimeout) {
        conn.setConnectTimeout(connectTimeout);
    }

    public final void setMaxPDULengthReceive(int maxPDULength) {
        ae.setMaxPDULengthReceive(maxPDULength);
    }

    public final void setMaxOpsInvoked(int maxOpsInvoked) {
        ae.setMaxOpsInvoked(maxOpsInvoked);
    }

    public final void setMaxOpsPerformed(int maxOps) {
        ae.setMaxOpsPerformed(maxOps);
    }

    public final void setPackPDV(boolean packPDV) {
        ae.setPackPDV(packPDV);
    }

    public final void setAssociationReaperPeriod(int period) {
        device.setAssociationReaperPeriod(period);
    }

    public final void setDimseRspTimeout(int timeout) {
        ae.setDimseRspTimeout(timeout);
    }

    public final void setRetrieveRspTimeout(int timeout) {
        ae.setRetrieveRspTimeout(timeout);
    }

    public final void setTcpNoDelay(boolean tcpNoDelay) {
        conn.setTcpNoDelay(tcpNoDelay);
    }

    public final void setAcceptTimeout(int timeout) {
        conn.setAcceptTimeout(timeout);
    }

    public final void setReleaseTimeout(int timeout) {
        conn.setReleaseTimeout(timeout);
    }

    public final void setSocketCloseDelay(int timeout) {
        conn.setSocketCloseDelay(timeout);
    }

    public final void setMaxPDULengthSend(int maxPDULength) {
        ae.setMaxPDULengthSend(maxPDULength);
    }

    public final void setReceiveBufferSize(int bufferSize) {
        conn.setReceiveBufferSize(bufferSize);
    }

    public final void setSendBufferSize(int bufferSize) {
        conn.setSendBufferSize(bufferSize);
    }

    public void addStoreTransferCapability(String cuid, String[] tsuids) {
        storeTransferCapability.add(new TransferCapability(
                cuid, tsuids, TransferCapability.SCP));
    }

    public void setEvalRetrieveAET(boolean evalRetrieveAET) {
       this.evalRetrieveAET = evalRetrieveAET;
    }

    public boolean isEvalRetrieveAET() {
        return evalRetrieveAET;
    }

    public void setNoExtNegotiation(boolean b) {
        this.noExtNegotiation = b;
    }

    public void setFuzzySemanticPersonNameMatching(boolean b) {
        this.fuzzySemanticPersonNameMatching = b;
    }

    public void setDateTimeMatching(boolean b) {
        this.dateTimeMatching = b;
    }

    public void setRelationQR(boolean b) {
        this.relationQR = b;
    }

    public final int getFailed() {
        return failed;
    }

    public final int getWarning() {
        return warning;
    }

    public final int getTotalRetrieved() {
        return completed + warning;
    }

    public void setCancelAfter(int limit) {
        this.cancelAfter = limit;
    }

    public void addMatchingKey(int[] tagPath, String value) {
        keys.putString(tagPath, null, value);
    }

    public void addReturnKey(int[] tagPath) {
        keys.putNull(tagPath, null);
    }

    public void configureTransferCapability(boolean ivrle) {
        String[] findcuids = qrlevel.getFindClassUids();
        String[] movecuids = moveDest != null ? qrlevel.getMoveClassUids()
                : EMPTY_STRING;
        String[] getcuids = cget ? qrlevel.getGetClassUids()
                : EMPTY_STRING;
        TransferCapability[] tcs = new TransferCapability[findcuids.length
                + privateFind.size() + movecuids.length + getcuids.length
                + storeTransferCapability.size()];
        int i = 0;
        for (String cuid : findcuids)
            tcs[i++] = mkFindTC(cuid, ivrle ? Constants.IVRLE_TS : Constants.NATIVE_LE_TS);
        for (String cuid : privateFind)
            tcs[i++] = mkFindTC(cuid, ivrle ? Constants.IVRLE_TS : Constants.DEFLATED_TS);
        for (String cuid : movecuids)
            tcs[i++] = mkRetrieveTC(cuid, ivrle ? Constants.IVRLE_TS : Constants.NATIVE_LE_TS);
        for (String cuid : getcuids)
            tcs[i++] = mkRetrieveTC(cuid, ivrle ? Constants.IVRLE_TS : Constants.NATIVE_LE_TS);
        for (TransferCapability tc : storeTransferCapability) {
            tcs[i++] = tc;
        }
        ae.setTransferCapability(tcs);
    }
    
    public void registerStorageService( ExtStorageService storageService) {
    	 if (!storeTransferCapability.isEmpty()) {
             ae.register(storageService);
         }
    }
    
    public String[] getTransferCapabilities() {
    	 String[] cuids = new String[storeTransferCapability.size()];
         int i = 0;
         for (TransferCapability tc : storeTransferCapability) {
             cuids[i++] = tc.getSopClass();
         }
         return cuids;
    }

    private TransferCapability mkRetrieveTC(String cuid, String[] ts) {
        ExtRetrieveTransferCapability tc = new ExtRetrieveTransferCapability(
                cuid, ts, TransferCapability.SCU);
        tc.setExtInfoBoolean(
                ExtRetrieveTransferCapability.RELATIONAL_RETRIEVAL, relationQR);
        if (noExtNegotiation)
            tc.setExtInfo(null);
        return tc;
    }

    private TransferCapability mkFindTC(String cuid, String[] ts) {
        ExtQueryTransferCapability tc = new ExtQueryTransferCapability(cuid,
                ts, TransferCapability.SCU);
        tc.setExtInfoBoolean(ExtQueryTransferCapability.RELATIONAL_QUERIES,
                relationQR);
        tc.setExtInfoBoolean(ExtQueryTransferCapability.DATE_TIME_MATCHING,
                dateTimeMatching);
        tc.setExtInfoBoolean(ExtQueryTransferCapability.FUZZY_SEMANTIC_PN_MATCHING,
                fuzzySemanticPersonNameMatching);
        if (noExtNegotiation)
            tc.setExtInfo(null);
        return tc;
    }

    public void setQueryLevel(Constants.QueryRetrieveLevel qrlevel) {
        this.qrlevel = qrlevel;
        keys.putString(Tag.QueryRetrieveLevel, VR.CS, qrlevel.getCode());
        for (int tag : qrlevel.getReturnKeys()) {
            keys.putNull(tag, null);
        }
    }

    public final void addPrivate(String cuid) {
        privateFind.add(cuid);
    }

    public void setCFind(boolean cfind) {
        this.cfind = cfind;
    }

    public boolean isCFind() {
        return cfind;
    }

    public void setCGet(boolean cget) {
        this.cget = cget;
    }

    public boolean isCGet() {
        return cget;
    }

    public void setMoveDest(String aet) {
        moveDest = aet;
    }

    public boolean isCMove() {
        return moveDest != null;
    }

    public void start() throws IOException { 
        if (conn.isListening()) {
            conn.bind(executor );
            System.out.println("Start Server listening on port " + conn.getPort());
        }
    }

    public void stop() {
        if (conn.isListening()) {
            conn.unbind();
        }
    }

    public void open() throws IOException, ConfigurationException,
            InterruptedException {
        assoc = ae.connect(remoteAE, executor);
    }

    public DicomObject getKeys() {
        return keys;
    }

    public List<DicomObject> query() throws IOException, InterruptedException {
        List<DicomObject> result = new ArrayList<DicomObject>();
        TransferCapability tc = selectFindTransferCapability();
        String cuid = tc.getSopClass();
        String tsuid = selectTransferSyntax(tc);
        if (tc.getExtInfoBoolean(ExtQueryTransferCapability.RELATIONAL_QUERIES)
                || containsUpperLevelUIDs(cuid)) {
            LOG.info("Send Query Request using {}:\n{}",
                    UIDDictionary.getDictionary().prompt(cuid), keys);
            DimseRSP rsp = assoc.cfind(cuid, priority, keys, tsuid, cancelAfter);
            while (rsp.next()) {
                DicomObject cmd = rsp.getCommand();
                if (CommandUtils.isPending(cmd)) {
                    DicomObject data = rsp.getDataset();
                    result.add(data);
                    LOG.info("Query Response #{}:\n{}", Integer.valueOf(result.size()), data);
                }
            }
        } else {
            List<DicomObject> upperLevelUIDs = queryUpperLevelUIDs(cuid, tsuid);
            List<DimseRSP> rspList = new ArrayList<DimseRSP>(upperLevelUIDs.size());
            for (int i = 0, n = upperLevelUIDs.size(); i < n; i++) {
                upperLevelUIDs.get(i).copyTo(keys);
                LOG.info("Send Query Request #{}/{} using {}:\n{}",
                        new Object[] {
                            Integer.valueOf(i+1),
                            Integer.valueOf(n),
                            UIDDictionary.getDictionary().prompt(cuid),
                            keys
                        });
                rspList.add(assoc.cfind(cuid, priority, keys, tsuid, cancelAfter));
            }
            for (int i = 0, n = rspList.size(); i < n; i++) {
                DimseRSP rsp = rspList.get(i);
                for (int j = 0; rsp.next(); ++j) {
                    DicomObject cmd = rsp.getCommand();
                    if (CommandUtils.isPending(cmd)) {
                        DicomObject data = rsp.getDataset();
                        result.add(data);
                        LOG.info("Query Response #{} for Query Request #{}/{}:\n{}",
                                new Object[]{ Integer.valueOf(j+1), Integer.valueOf(i+1), Integer.valueOf(n), data });
                    }
                }
            }
        }
        return result;
    }

    @SuppressWarnings("fallthrough")
    private boolean containsUpperLevelUIDs(String cuid) {
        switch (qrlevel) {
        case IMAGE:
            if (!keys.containsValue(Tag.SeriesInstanceUID)) {
                return false;
            }
            // fall through
        case SERIES:
            if (!keys.containsValue(Tag.StudyInstanceUID)) {
                return false;
            }
            // fall through
        case STUDY:
            if (Arrays.asList(Constants.PATIENT_LEVEL_FIND_CUID).contains(cuid)
                    && !keys.containsValue(Tag.PatientID)) {
                return false;
            }
            // fall through
        case PATIENT:
            // fall through
        }
        return true;
    }

    private List<DicomObject> queryUpperLevelUIDs(String cuid, String tsuid)
            throws IOException, InterruptedException {
        List<DicomObject> keylist = new ArrayList<DicomObject>();
        if (Arrays.asList(Constants.PATIENT_LEVEL_FIND_CUID).contains(cuid)) {
            queryPatientIDs(cuid, tsuid, keylist);
            if (qrlevel == Constants.QueryRetrieveLevel.STUDY) {
                return keylist;
            }
            keylist = queryStudyOrSeriesIUIDs(cuid, tsuid, keylist,
                    Tag.StudyInstanceUID, Constants.STUDY_MATCHING_KEYS, Constants.QueryRetrieveLevel.STUDY);
        } else {
            keylist.add(new BasicDicomObject());
            keylist = queryStudyOrSeriesIUIDs(cuid, tsuid, keylist,
                    Tag.StudyInstanceUID, Constants.PATIENT_STUDY_MATCHING_KEYS, Constants.QueryRetrieveLevel.STUDY);
        }
        if (qrlevel == QueryRetrieveLevel.IMAGE) {
            keylist = queryStudyOrSeriesIUIDs(cuid, tsuid, keylist,
                    Tag.SeriesInstanceUID, Constants.SERIES_MATCHING_KEYS, QueryRetrieveLevel.SERIES);
        }
        return keylist;
    }

    private void queryPatientIDs(String cuid, String tsuid,
            List<DicomObject> keylist) throws IOException, InterruptedException {
        String patID = keys.getString(Tag.PatientID);
        String issuer = keys.getString(Tag.IssuerOfPatientID);
        if (patID != null) {
            DicomObject patIdKeys = new BasicDicomObject();
            patIdKeys.putString(Tag.PatientID, VR.LO, patID);
            if (issuer != null) {
                patIdKeys.putString(Tag.IssuerOfPatientID, VR.LO, issuer);
            }
            keylist.add(patIdKeys);
        } else {
            DicomObject patLevelQuery = new BasicDicomObject();
            keys.subSet(PATIENT_MATCHING_KEYS).copyTo(patLevelQuery);
            patLevelQuery.putNull(Tag.PatientID, VR.LO);
            patLevelQuery.putNull(Tag.IssuerOfPatientID, VR.LO);
            patLevelQuery.putString(Tag.QueryRetrieveLevel, VR.CS, "PATIENT");
            LOG.info("Send Query Request using {}:\n{}",
                    UIDDictionary.getDictionary().prompt(cuid), patLevelQuery);
            DimseRSP rsp = assoc.cfind(cuid, priority, patLevelQuery, tsuid,
                    Integer.MAX_VALUE);
            for (int i = 0; rsp.next(); ++i) {
                DicomObject cmd = rsp.getCommand();
                if (CommandUtils.isPending(cmd)) {
                    DicomObject data = rsp.getDataset();
                    LOG.info("Query Response #{}:\n{}", Integer.valueOf(i+1), data);
                    DicomObject patIdKeys = new BasicDicomObject();
                    patIdKeys.putString(Tag.PatientID, VR.LO,
                            data.getString(Tag.PatientID));
                    issuer = keys.getString(Tag.IssuerOfPatientID);
                    if (issuer != null) {
                        patIdKeys.putString(Tag.IssuerOfPatientID, VR.LO,
                                issuer);
                    }
                    keylist.add(patIdKeys);
                }
            }
        }
    }

    private List<DicomObject> queryStudyOrSeriesIUIDs(String cuid, String tsuid,
            List<DicomObject> upperLevelIDs, int uidTag, int[] matchingKeys,
            QueryRetrieveLevel qrLevel) throws IOException,
            InterruptedException {
        List<DicomObject> keylist = new ArrayList<DicomObject>();
        String uid = keys.getString(uidTag);
        for (DicomObject upperLevelID : upperLevelIDs) {
            if (uid != null) {
                DicomObject suidKey = new BasicDicomObject();
                upperLevelID.copyTo(suidKey);
                suidKey.putString(uidTag, VR.UI, uid);
                keylist.add(suidKey);
            } else {
                DicomObject keys2 = new BasicDicomObject();
                keys.subSet(matchingKeys).copyTo(keys2);
                upperLevelID.copyTo(keys2);
                keys2.putNull(uidTag, VR.UI);
                keys2.putString(Tag.QueryRetrieveLevel, VR.CS, qrLevel.getCode());
                LOG.info("Send Query Request using {}:\n{}",
                        UIDDictionary.getDictionary().prompt(cuid), keys2);
                DimseRSP rsp = assoc.cfind(cuid, priority, keys2,
                        tsuid, Integer.MAX_VALUE);
                for (int i = 0; rsp.next(); ++i) {
                    DicomObject cmd = rsp.getCommand();
                    if (CommandUtils.isPending(cmd)) {
                        DicomObject data = rsp.getDataset();
                        LOG.info("Query Response #{}:\n{}", Integer.valueOf(i+1), data);
                        DicomObject suidKey = new BasicDicomObject();
                        upperLevelID.copyTo(suidKey);
                        suidKey.putString(uidTag, VR.UI, data.getString(uidTag));
                        keylist.add(suidKey);
                    }
                }
            }
        }
        return keylist;
    }

    public TransferCapability selectFindTransferCapability()
            throws NoPresentationContextException {
        TransferCapability tc;
        if ((tc = selectTransferCapability(privateFind)) != null)
            return tc;
        if ((tc = selectTransferCapability(qrlevel.getFindClassUids())) != null)
            return tc;
        throw new NoPresentationContextException(UIDDictionary.getDictionary()
                .prompt(qrlevel.getFindClassUids()[0])
                + " not supported by " + remoteAE.getAETitle());
    }

    public String selectTransferSyntax(TransferCapability tc) {
        String[] tcuids = tc.getTransferSyntax();
        if (Arrays.asList(tcuids).indexOf(UID.DeflatedExplicitVRLittleEndian) != -1)
            return UID.DeflatedExplicitVRLittleEndian;
        return tcuids[0];
    }

    public void move(List<DicomObject> findResults)
            throws IOException, InterruptedException {
        if (moveDest == null)
            throw new IllegalStateException("moveDest == null");
        TransferCapability tc = selectTransferCapability(qrlevel.getMoveClassUids());
        if (tc == null)
            throw new NoPresentationContextException(UIDDictionary
                    .getDictionary().prompt(qrlevel.getMoveClassUids()[0])
                    + " not supported by " + remoteAE.getAETitle());
        String cuid = tc.getSopClass();
        String tsuid = selectTransferSyntax(tc);
        for (int i = 0, n = Math.min(findResults.size(), cancelAfter); i < n; ++i) {
            DicomObject keys = findResults.get(i).subSet(Constants.MOVE_KEYS);
            if (isEvalRetrieveAET() && containsMoveDest(
                    findResults.get(i).getStrings(Tag.RetrieveAETitle))) {
                LOG.info("Skipping {}:\n{}",
                        UIDDictionary.getDictionary().prompt(cuid), keys);
            } else {
                LOG.info("Send Retrieve Request using {}:\n{}",
                        UIDDictionary.getDictionary().prompt(cuid), keys);
                assoc.cmove(cuid, priority, keys, tsuid, moveDest, rspHandler);
            }
        }
        assoc.waitForDimseRSP();
    }


    private boolean containsMoveDest(String[] retrieveAETs) {
        if (retrieveAETs != null) {
            for (String aet : retrieveAETs) {
                if (moveDest.equals(aet)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void get(List<DicomObject> findResults)
            throws IOException, InterruptedException {
        TransferCapability tc = selectTransferCapability(qrlevel.getGetClassUids());
        if (tc == null)
            throw new NoPresentationContextException(UIDDictionary
                    .getDictionary().prompt(qrlevel.getGetClassUids()[0])
                    + " not supported by " + remoteAE.getAETitle());
        String cuid = tc.getSopClass();
        String tsuid = selectTransferSyntax(tc);
        for (int i = 0, n = Math.min(findResults.size(), cancelAfter); i < n; ++i) {
            DicomObject keys = findResults.get(i).subSet(MOVE_KEYS);
            LOG.info("Send Retrieve Request using {}:\n{}",
                    UIDDictionary.getDictionary().prompt(cuid), keys);
            assoc.cget(cuid, priority, keys, tsuid, rspHandler);
        }
        assoc.waitForDimseRSP();
    }
    
    private final DimseRSPHandler rspHandler = new DimseRSPHandler() {
        @Override
        public void onDimseRSP(Association as, DicomObject cmd,
                DicomObject data) {
            ExtDcmQR.this.onMoveRSP(as, cmd, data);
        }
    };

    protected void onMoveRSP(Association as, DicomObject cmd, DicomObject data) {
        if (!CommandUtils.isPending(cmd)) {
            completed += cmd.getInt(Tag.NumberOfCompletedSuboperations);
            warning += cmd.getInt(Tag.NumberOfWarningSuboperations);
            failed += cmd.getInt(Tag.NumberOfFailedSuboperations);
        }

    }

    public TransferCapability selectTransferCapability(String[] cuid) {
        TransferCapability tc;
        for (int i = 0; i < cuid.length; i++) {
            tc = assoc.getTransferCapabilityAsSCU(cuid[i]);
            if (tc != null)
                return tc;
        }
        return null;
    }

    public TransferCapability selectTransferCapability(List<String> cuid) {
        TransferCapability tc;
        for (int i = 0, n = cuid.size(); i < n; i++) {
            tc = assoc.getTransferCapabilityAsSCU(cuid.get(i));
            if (tc != null)
                return tc;
        }
        return null;
    }

    public void close() throws InterruptedException {
        assoc.release(true);
    }

    public void initTLS() throws GeneralSecurityException, IOException {
        KeyStore keyStore = loadKeyStore(keyStoreURL, keyStorePassword);
        KeyStore trustStore = loadKeyStore(trustStoreURL, trustStorePassword);
        device.initTLS(keyStore,
                keyPassword != null ? keyPassword : keyStorePassword,
                trustStore);
    }
    
    private static KeyStore loadKeyStore(String url, char[] password)
            throws GeneralSecurityException, IOException {
        KeyStore key = KeyStore.getInstance(toKeyStoreType(url));
        InputStream in = openFileOrURL(url);
        try {
            key.load(in, password);
        } finally {
            in.close();
        }
        return key;
    }

    private static InputStream openFileOrURL(String url) throws IOException {
        if (url.startsWith("resource:")) {
            return ExtDcmQR.class.getClassLoader().getResourceAsStream(
                    url.substring(9));
        }
        try {
            return new URL(url).openStream();
        } catch (MalformedURLException e) {
            return new FileInputStream(url);
        }
    }

    private static String toKeyStoreType(String fname) {
        return fname.endsWith(".p12") || fname.endsWith(".P12")
                 ? "PKCS12" : "JKS";
    }
}