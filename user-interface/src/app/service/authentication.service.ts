import {Injectable} from '@angular/core';
import {Router} from "@angular/router";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {CookieService} from "ngx-cookie-service";
import {Observable} from "rxjs/internal/Observable";
import {ResetPassword} from "../domain/ResetPassword";
import {ApiResponse} from "../domain/ApiResponse";
import {AuthRequest} from "../domain/AuthRequest";
import {AuthResponse} from "../domain/AuthResponse";

@Injectable({
    providedIn: 'root'
})
export class AuthenticationService {
    private createUserUrl = "api/uaa/auth/sign-up";
    private tokenRequest = 'api/uaa/auth/login';
    private resendVerificationEmailUrl = "api/uaa/auth/verification/resend";
    private verificationEmailUrl = "api/uaa/auth/verification";
    private forgotPasswordUrl = "api/uaa/auth/password/forgot";

    private changePasswordUrl = "api/uaa/users/change/password";

    constructor(private router: Router, private http: HttpClient, private cookieService: CookieService) {
    }

    public createUser(user: AuthRequest): Observable<ApiResponse> {
        return this.http.post<ApiResponse>(this.createUserUrl, user);
    }

    public obtainAccessToken(authRequest: AuthRequest): Observable<AuthResponse> {
        return this.http.post<AuthResponse>(this.tokenRequest, authRequest);
    }

    public resendVerificationEmail(email: String): Observable<ApiResponse> {
        return this.http.get<ApiResponse>(this.resendVerificationEmailUrl + "?email=" + email);
    }

    public forgotPassword(email: String): Observable<ApiResponse> {
        return this.http.get<ApiResponse>(this.forgotPasswordUrl + "?email=" + email);
    }

    public resetPassword(resetPassword: ResetPassword): Observable<ApiResponse> {
        return this.http.put<ApiResponse>(this.forgotPasswordUrl, resetPassword);
    }

    public verifyEmail(toke: String): Observable<ApiResponse> {
        return this.http.get<ApiResponse>(this.verificationEmailUrl + '?token=' + toke);
    }

    public updatePassword(password: String): Observable<ApiResponse> {
        let token = this.getOauthToken();

        let headers = new HttpHeaders({'Authorization': 'Bearer ' + token});
        let options = {
            headers: headers
        };

        return this.http.post<ApiResponse>(this.changePasswordUrl, password, options);
    }

    public getOauthToken(): string {
        return this.cookieService.get('access_token');
    }

    public getUsername(): string {
        return this.cookieService.get("username");
    }

    public checkCredentials() {
        if (!this.cookieService.check('access_token')) {
            this.router.navigate(['']);
        }
    }

    public isUserLogin(): boolean {
        return this.cookieService.check('access_token');
    }

    public logout() {
        this.cookieService.delete('access_token');
        this.cookieService.delete('username');
        this.router.navigate(['']);
        localStorage.clear();
    }

    public saveCredentials(token, username, rememberMe: Boolean) {
        let expireDate;

        if (rememberMe) {
            expireDate = new Date(Date.now() + (1000 * token.expires_in));
        } else {
            expireDate = Date.now();
        }

        this.cookieService.set("access_token", token.access_token, expireDate);
        this.cookieService.set("username", username, expireDate);

        this.router.navigate(['/statistics']);
        window.location.reload();
    }

}
