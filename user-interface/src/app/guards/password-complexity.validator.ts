import { FormGroup } from '@angular/forms';

export function PasswordComplexity(controlName: string) {
    return (formGroup: FormGroup) => {
        const control = formGroup.controls[controlName];

        if (control.errors && !control.errors.passwordComplexity) {
            // return if another validator has already found an error on the matchingControl
            return;
        }

        if (!/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{8,}$/.test(control.value)) {
            control.setErrors({ passwordComplexity: true });
        } else {
            control.setErrors(null);
        }
    }
}
