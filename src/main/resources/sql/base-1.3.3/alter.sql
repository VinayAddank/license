/**
 * @author 100rabh.rawat
 * @description Added UNIQUE constraint in application table for application_number column.
 * Date 29-08-2017
 */
ALTER TABLE ONLY application
    ADD CONSTRAINT uk_application_number_col_application UNIQUE (application_number);