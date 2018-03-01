package org.rta.citizen.common.service;

import org.rta.citizen.common.model.InvoiceModel;

public interface InvoiceService {

	public InvoiceModel getInvoiceDetails(long sessionId , String appNo);
	public InvoiceModel getInvoiceDLDetails(long sessionId, String appNo);
	public InvoiceModel getInvoiceUsersDetails(long sessionId);
	public Boolean attachments4Communication(String appNo , String attachmentURL);
    public InvoiceModel getReceiptUsersDetails(String appNo);
}
