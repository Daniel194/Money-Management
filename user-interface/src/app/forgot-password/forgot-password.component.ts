import {Component, OnInit} from '@angular/core';
import {AuthenticationService} from "../service/authentication.service";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ToastrService} from "ngx-toastr";
import {ActivatedRoute, Router} from "@angular/router";
import {PasswordErrorStateMatcher} from "../util/password-error-state-matcher";
import {ResetPassword} from "../domain/ResetPassword";
import {User} from "../domain/User";

@Component({
    selector: 'app-forgot-password',
    templateUrl: './forgot-password.component.html',
    styleUrls: ['./forgot-password.component.css']
})
export class ForgotPasswordComponent implements OnInit {

    private token: String;

    public changePasswordForm: FormGroup;
    public matcher: PasswordErrorStateMatcher;
    public hidePassword1: Boolean;
    public hidePassword2: Boolean;

    constructor(private authService: AuthenticationService,
                private fb: FormBuilder,
                private toaster: ToastrService,
                private router: Router,
                private activatedRoute: ActivatedRoute) {
    }

    ngOnInit() {

        if (AuthenticationService.isUserLogin()) {
            this.router.navigate(['/statistics']);
        }

        this.activatedRoute.queryParams.subscribe(params => {
            this.token = params['token'];

            if (this.token == null) {
                this.goToLogin();
            }
        });

        this.hidePassword1 = true;
        this.hidePassword2 = true;
        this.matcher = new PasswordErrorStateMatcher();

        this.changePasswordForm = this.fb.group({
            password: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(40)]],
            repeatPassword: ['']
        }, {validator: this.checkPasswords});
    }

    onSubmitChangePassword() {
        let resetPassword: ResetPassword = new ResetPassword();
        resetPassword.token = this.token;
        resetPassword.password = this.changePasswordForm.controls.password.value;

        this.authService.resetPassword(resetPassword).subscribe(message => {
            this.toaster.info(message.toString());
        })
    }

    goToLogin() {
        this.router.navigate(['/']);
    }

    checkPasswords(group: FormGroup) {
        let pass = group.controls.password.value;
        let confirmPass = group.controls.repeatPassword.value;

        return pass === confirmPass ? null : {notSame: true}
    }
}
