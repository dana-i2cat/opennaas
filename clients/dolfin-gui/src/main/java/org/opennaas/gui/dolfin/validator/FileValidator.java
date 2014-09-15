package org.opennaas.gui.dolfin.validator;

import org.opennaas.gui.dolfin.beans.UploadedFile;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 *
 * @author Josep Batallé <josep.batalle@i2cat.net>
 */

public class FileValidator implements Validator {

    @Override
    public void validate(Object uploadedFile, Errors errors) {
        UploadedFile file = (UploadedFile) uploadedFile;
        if (file.getFile().getSize() == 0) {
            errors.rejectValue("file", "uploadForm.salectFile", "Please select a file!");
        }
    }

    @Override
    public boolean supports(Class<?> type) {
        return false;
    }
}
