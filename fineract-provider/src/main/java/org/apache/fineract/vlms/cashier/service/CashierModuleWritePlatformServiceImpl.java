/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.vlms.cashier.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.PersistenceException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.fineract.infrastructure.codes.exception.CodeNotFoundException;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.vlms.cashier.domain.CashierModuleTask;
import org.apache.fineract.vlms.cashier.domain.CashierModuleTaskRepository;
import org.apache.fineract.vlms.cashier.domain.HLPayment;
import org.apache.fineract.vlms.cashier.domain.HLPaymentDetails;
import org.apache.fineract.vlms.cashier.domain.HLPaymentDetailsRepository;
import org.apache.fineract.vlms.cashier.domain.HLPaymentRepository;
import org.apache.fineract.vlms.cashier.domain.Voucher;
import org.apache.fineract.vlms.cashier.domain.VoucherDetails;
import org.apache.fineract.vlms.cashier.domain.VoucherDetailsRepository;
import org.apache.fineract.vlms.cashier.domain.VoucherRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CashierModuleWritePlatformServiceImpl implements CashierModuleWritePlatformService {

    private final PlatformSecurityContext context;
    private final HLPaymentRepository hlPaymentRepository;
    private final HLPaymentDetailsRepository hlPaymentDetailsRepository;
    private final VoucherRepository voucherRepository;
    private final VoucherDetailsRepository voucherDetailsRepository;
    private final CashierModuleTaskRepository cashierModuleTaskRepository;

    @Autowired
    public CashierModuleWritePlatformServiceImpl(final PlatformSecurityContext context, final HLPaymentRepository hlPaymentRepository,
            final VoucherRepository voucherRepository, final VoucherDetailsRepository voucherDetailsRepository,
            final HLPaymentDetailsRepository hlPaymentDetailsRepository, final CashierModuleTaskRepository cashierModuleTaskRepository) {
        this.context = context;
        this.hlPaymentRepository = hlPaymentRepository;
        this.voucherRepository = voucherRepository;
        this.voucherDetailsRepository = voucherDetailsRepository;
        this.hlPaymentDetailsRepository = hlPaymentDetailsRepository;
        this.cashierModuleTaskRepository = cashierModuleTaskRepository;
    }

    private static final Logger LOG = LoggerFactory.getLogger(CashierModuleWritePlatformServiceImpl.class);

    @Transactional
    @Override
    @CacheEvict(value = "hlpayment", key = "T(org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('cv')")
    public CommandProcessingResult createHLPayment(final JsonCommand command) {

        try {
            final HLPayment hlPayment = HLPayment.fromJson(command);

            this.hlPaymentRepository.save(hlPayment);

            List<HLPaymentDetails> hlPaymentDetails = this.fetchHLPaymentData(hlPayment, command.parsedJson().getAsJsonObject());

            System.out.println(" hlPaymentDetails : " + hlPaymentDetails);

            this.hlPaymentDetailsRepository.saveAll(hlPaymentDetails);
            System.out.println(" hlPaymentDetails saved ");
            // this.hlPaymentDetailsRepository.flush();

            return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(hlPayment.getId()).build();
            // .withEntityId(code.getId())
        } catch (final JpaSystemException | DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve.getMostSpecificCause(), dve);
            return CommandProcessingResult.empty();
        } catch (final PersistenceException ee) {
            Throwable throwable = ExceptionUtils.getRootCause(ee.getCause());
            handleDataIntegrityIssues(command, throwable, ee);
            return CommandProcessingResult.empty();
        }

    }

    @Transactional
    @Override
    @CacheEvict(value = "voucher", key = "T(org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('cv')")
    public CommandProcessingResult createVoucher(final JsonCommand command) {

        try {

            final Voucher voucher = Voucher.fromJson(command);

            this.voucherRepository.save(voucher);

            List<VoucherDetails> voucherDetails = this.fetchVoucherData(voucher, command.parsedJson().getAsJsonObject());

            System.out.println(" voucherDetails : " + voucherDetails);
            // for (VoucherDetails voucherDetail : voucherDetails) {
            // System.out.println(" voucherDetail : " + voucherDetail);
            this.voucherDetailsRepository.saveAll(voucherDetails);
            this.voucherDetailsRepository.flush();
            // }

            // this.voucherDetailsRepository.saveAll(voucherDetails);

            return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(voucher.getId()).build();
            // .withEntityId(code.getId())
        } catch (final JpaSystemException | DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve.getMostSpecificCause(), dve);
            return CommandProcessingResult.empty();
        } catch (final PersistenceException ee) {
            Throwable throwable = ExceptionUtils.getRootCause(ee.getCause());
            handleDataIntegrityIssues(command, throwable, ee);
            return CommandProcessingResult.empty();
        }

    }

    public List<VoucherDetails> fetchVoucherData(final Voucher voucher, final JsonObject command) {
        List<VoucherDetails> voucherDatas = new ArrayList<>();

        if (command.has("voucher")) {
            final JsonArray voucherDataArray = command.getAsJsonArray("voucher");
            System.out.println("voucherDataArray size: " + voucherDataArray.size());
            if (voucherDataArray != null && voucherDataArray.size() > 0) {
                int i = 0;
                do {
                    final JsonObject jsonObject = voucherDataArray.get(i).getAsJsonObject();
                    BigDecimal credit = null;
                    BigDecimal debit = null;
                    String particulars = null;
                    System.out.println("--");
                    if (jsonObject.has("credit") && jsonObject.get("credit").isJsonPrimitive()
                            && StringUtils.isNotBlank(jsonObject.get("credit").getAsString())) {

                        credit = jsonObject.getAsJsonPrimitive("credit").getAsBigDecimal();
                        System.out.println("credit: " + credit);
                    }

                    if (jsonObject.has("debit") && jsonObject.get("debit").isJsonPrimitive()
                            && StringUtils.isNotBlank(jsonObject.get("debit").getAsString())) {

                        debit = jsonObject.getAsJsonPrimitive("debit").getAsBigDecimal();
                        System.out.println("debit: " + debit);
                    }

                    if (jsonObject.has("particulars") && jsonObject.get("particulars").isJsonPrimitive()) {

                        particulars = jsonObject.getAsJsonPrimitive("particulars").getAsString();
                        System.out.println("particulars: " + particulars);
                    }

                    voucherDatas.add(new VoucherDetails(voucher, credit, debit, particulars));
                    i++;
                    System.out.println("i " + i);
                }

                while (i < voucherDataArray.size());

            }
        }
        return voucherDatas;
    }

    public List<HLPaymentDetails> fetchHLPaymentData(final HLPayment hlPayment, final JsonObject command) {
        List<HLPaymentDetails> hlPaymentDatas = new ArrayList<>();

        if (command.has("hl")) {
            final JsonArray hlDataArray = command.getAsJsonArray("hl");
            System.out.println("hl size: " + hlDataArray.size());
            if (hlDataArray != null && hlDataArray.size() > 0) {
                int i = 0;
                do {
                    final JsonObject jsonObject = hlDataArray.get(i).getAsJsonObject();
                    String agtNo = null;
                    String customerName = null;
                    BigDecimal actualAmount = null;
                    BigDecimal postAmount = null;
                    Date expiryDate = null;
                    String policyNo = null;
                    String insuranceCompany = null;
                    String remarks = null;
                    System.out.println("--");

                    if (jsonObject.has("credit") && jsonObject.get("agtno").isJsonPrimitive()
                            && StringUtils.isNotBlank(jsonObject.get("agtno").getAsString())) {

                        agtNo = jsonObject.getAsJsonPrimitive("agtno").getAsString();
                        System.out.println("agtNo: " + agtNo);
                    }

                    if (jsonObject.has("customerName") && jsonObject.get("customerName").isJsonPrimitive()
                            && StringUtils.isNotBlank(jsonObject.get("customerName").getAsString())) {

                        customerName = jsonObject.getAsJsonPrimitive("customerName").getAsString();
                        System.out.println("customerName: " + customerName);
                    }

                    if (jsonObject.has("actualAmount") && jsonObject.get("actualAmount").isJsonPrimitive()
                            && StringUtils.isNotBlank(jsonObject.get("actualAmount").getAsString())) {

                        actualAmount = jsonObject.getAsJsonPrimitive("actualAmount").getAsBigDecimal();
                        System.out.println("actualAmount: " + actualAmount);
                    }

                    if (jsonObject.has("postAmount") && jsonObject.get("postAmount").isJsonPrimitive()
                            && StringUtils.isNotBlank(jsonObject.get("postAmount").getAsString())) {

                        postAmount = jsonObject.getAsJsonPrimitive("postAmount").getAsBigDecimal();
                        System.out.println("postAmount: " + postAmount);
                    }

                    if (jsonObject.has("expiryDate") && jsonObject.get("expiryDate").isJsonPrimitive()
                            && StringUtils.isNotBlank(jsonObject.get("expiryDate").getAsString())) {

                        String date = jsonObject.getAsJsonPrimitive("expiryDate").getAsString();

                        DateFormat df = new SimpleDateFormat("dd MMMM yyyy");
                        try {
                            expiryDate = df.parse(date);
                        } catch (ParseException Ex) {
                            LOG.error("Error occured while converting Date(String) to SimpleDateFormat", Ex);
                        }

                    }

                    if (jsonObject.has("policyNo") && jsonObject.get("policyNo").isJsonPrimitive()) {

                        policyNo = jsonObject.getAsJsonPrimitive("policyNo").getAsString();
                        System.out.println("policyNo: " + policyNo);
                    }

                    if (jsonObject.has("insuranceCompany") && jsonObject.get("insuranceCompany").isJsonPrimitive()) {

                        insuranceCompany = jsonObject.getAsJsonPrimitive("insuranceCompany").getAsString();
                        System.out.println("insuranceCompany: " + insuranceCompany);
                    }

                    if (jsonObject.has("remark") && jsonObject.get("remark").isJsonPrimitive()) {

                        remarks = jsonObject.getAsJsonPrimitive("remark").getAsString();
                        System.out.println("remark: " + remarks);
                    }

                    hlPaymentDatas.add(new HLPaymentDetails(hlPayment, agtNo, customerName, actualAmount, postAmount, expiryDate, policyNo,
                            insuranceCompany, remarks));
                    i++;
                    System.out.println("i " + i);
                }

                while (i < hlDataArray.size());

            }
        }
        return hlPaymentDatas;
    }

    @Transactional
    @Override
    @CacheEvict(value = "fetask", key = "T(org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('cv')")
    public CommandProcessingResult editCashierModuleTask(final Long taskId, final JsonCommand command) {

        try {
            this.context.authenticatedUser();

            // this.fromApiJsonDeserializer.validateForCreate(command.json());

            /*
             * final Code code = retrieveCodeBy(codeId); final Map<String, Object> changes = code.update(command);
             */

            final CashierModuleTask cashierModuleTask = retrieveTaskBy(taskId);
            final Map<String, Object> changes = cashierModuleTask.update(command);

            if (!changes.isEmpty()) {
                this.cashierModuleTaskRepository.save(cashierModuleTask);
            }

            /*
             * final FETask feTask = FETask.fromJson(command); this.feTaskRepository.save(feTask);
             */

            return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(taskId).build();
            // .withEntityId(code.getId())
        } catch (final JpaSystemException | DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve.getMostSpecificCause(), dve);
            return CommandProcessingResult.empty();
        } catch (final PersistenceException ee) {
            Throwable throwable = ExceptionUtils.getRootCause(ee.getCause());
            handleDataIntegrityIssues(command, throwable, ee);
            return CommandProcessingResult.empty();
        }

    }

    private CashierModuleTask retrieveTaskBy(final Long taskId) {
        return this.cashierModuleTaskRepository.findById(taskId).orElseThrow(() -> new CodeNotFoundException(taskId.toString()));
    }

    private void handleDataIntegrityIssues(final JsonCommand command, final Throwable realCause, final Exception dve) {

        if (realCause.getMessage().contains("mobile_no")) {
            final String mobileNo = command.stringValueOfParameterNamed("mobileNo");
            throw new PlatformDataIntegrityException("error.msg.client.duplicate.mobileNo",
                    "Client with mobileNo `" + mobileNo + "` already exists", "mobileNo", mobileNo);
        }

        throw new PlatformDataIntegrityException("error.msg.client.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }

}
