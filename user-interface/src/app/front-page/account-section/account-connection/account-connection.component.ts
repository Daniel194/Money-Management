import {ChangeDetectorRef, Component} from '@angular/core';
import {animate, state, style, transition, trigger} from '@angular/animations';
import {User} from '../../../domain/User';
import {FormBuilder, FormControl, FormGroup, FormGroupDirective, NgForm, Validators} from "@angular/forms";
import {ErrorStateMatcher} from "@angular/material";
import {AuthenticationService} from "../../../service/authentication.service";
import {ToastrService} from "ngx-toastr";
import {AccountSection} from "../account-section";

@Component({
    selector: 'app-account-connection',
    templateUrl: './account-connection.component.html',
    styleUrls: ['./account-connection.component.css'],
    animations: [
        trigger('flipState', [
            state('active', style({
                transform: 'rotateY(179.9deg)'
            })),
            state('inactive', style({
                transform: 'rotateY(0)'
            })),
            transition('active => inactive', animate('1000ms ease-out')),
            transition('inactive => active', animate('1000ms ease-in'))
        ]),
        trigger('showLogin', [
            state('true', style({opacity: 1, transform: 'translateY(0%)'})),
            state('false', style({opacity: 0, transform: 'translateY(100%)'})),
            transition('false => true', [animate('1s 0.5s ease-in')])
        ])
    ]
})
export class AccountConnectionComponent extends AccountSection {
    public hidePassword1: Boolean;
    public hidePassword2: Boolean;
    public hidePassword3: Boolean;

    public flip: String;

    public loginForm: FormGroup;
    public createAccountForm: FormGroup;

    public matcher: PasswordErrorStateMatcher;

    constructor(private fb: FormBuilder, private toaster: ToastrService, private authService: AuthenticationService,
                cdr: ChangeDetectorRef) {

        super(cdr);

        this.flip = 'inactive';
        this.hidePassword1 = true;
        this.hidePassword2 = true;
        this.hidePassword3 = true;
        this.matcher = new PasswordErrorStateMatcher();

        this.loginForm = fb.group({
            email: ['', [Validators.required, Validators.email, Validators.minLength(3), Validators.maxLength(64)]],
            password: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(40)]]
        });

        this.createAccountForm = fb.group({
            email: ['', [Validators.required, Validators.email, Validators.minLength(3), Validators.maxLength(64)]],
            password: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(40)]],
            repeatPassword: ['']
        }, {validator: this.checkPasswords});
    }

    onSubmitCreateAccount() {
        let user = new User();
        user.username = this.createAccountForm.controls.email.value;
        user.password = this.createAccountForm.controls.password.value;

        this.authService.createUser(user).subscribe(
            () => this.displaySuccessMessage("The user was created successfully ! \n \n A verification email has been sent to you, " +
                "please confirm it before to login !"),
            error => this.displayErrorMessage(error.error.message));

    }

    onSubmitLogin() {
        let user = new User();
        user.username = this.loginForm.controls.email.value;
        user.password = this.loginForm.controls.password.value;

        this.authService.obtainAccessToken(user).subscribe(
            data => this.authService.saveCredentials(data, user.username),
            error => this.displayErrorMessage(error.error.error_description))
    }

    toggleFlip() {
        this.flip = (this.flip === 'inactive') ? 'active' : 'inactive';
    }

    checkPasswords(group: FormGroup) {
        let pass = group.controls.password.value;
        let confirmPass = group.controls.repeatPassword.value;

        return pass === confirmPass ? null : {notSame: true}
    }

    private displaySuccessMessage(message: string) {
        this.toaster.success(message, 'Success');
    }

    private displayErrorMessage(message: string) {
        this.toaster.error(message, 'Error');
    }

}

class PasswordErrorStateMatcher implements ErrorStateMatcher {
    isErrorState(control: FormControl | null, form: FormGroupDirective | NgForm | null): boolean {
        const invalidCtrl = !!(control && control.invalid && control.parent.dirty);
        const invalidParent = !!(control && control.parent && control.parent.invalid && control.parent.dirty);

        return (invalidCtrl || invalidParent);
    }
}