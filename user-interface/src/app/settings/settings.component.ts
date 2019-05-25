import {Component, OnInit} from '@angular/core';
import {AuthenticationService} from "../service/authentication.service";
import {Router} from "@angular/router";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ToastrService} from "ngx-toastr";
import {PasswordMatchValidator} from "../guards/password-match.validator";

@Component({
    selector: 'app-settings',
    templateUrl: './settings.component.html',
    styleUrls: ['./settings.component.css']
})
export class SettingsComponent implements OnInit {

    public changePasswordForm: FormGroup;
    public matcher: PasswordMatchValidator;
    public hidePassword1: Boolean;
    public hidePassword2: Boolean;

    constructor(private authService: AuthenticationService,
                private fb: FormBuilder,
                private toaster: ToastrService,
                private router: Router) {

        this.hidePassword1 = true;
        this.hidePassword2 = true;
        this.matcher = new PasswordMatchValidator();

        this.changePasswordForm = this.fb.group({
            password: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(40)]],
            repeatPassword: ['']
        }, {validator: this.checkPasswords});
    }

    ngOnInit() {
    }

    onSubmitChangePassword() {
        this.authService.updatePassword(this.changePasswordForm.controls.password.value)
            .subscribe(() => this.toaster.success("Your password have been updated !"))
    }

    public logout() {
        this.authService.logout();
    }

    public navigateToAccount() {
        this.router.navigate(['/account']);
    }

    public navigateToStatistics() {
        this.router.navigate(['/statistics']);
    }

    checkPasswords(group: FormGroup) {
        let pass = group.controls.password.value;
        let confirmPass = group.controls.repeatPassword.value;

        return pass === confirmPass ? null : {notSame: true}
    }
}
