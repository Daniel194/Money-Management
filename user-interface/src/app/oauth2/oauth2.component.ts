import {Component, OnInit} from '@angular/core';
import {AuthenticationService} from "../service/authentication.service";
import {ActivatedRoute, Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";

@Component({
    selector: 'app-oauth2',
    templateUrl: './oauth2.component.html',
    styleUrls: ['./oauth2.component.css']
})
export class Oauth2Component implements OnInit {

    constructor(private router: Router,
                private activatedRoute: ActivatedRoute,
                private authService: AuthenticationService,
                public toaster: ToastrService) {
    }

    ngOnInit() {

        if (AuthenticationService.isUserLogin()) {
            this.router.navigate(['/statistics']);
        }

        this.activatedRoute.queryParams.subscribe(params => {
            let token = params['token'];
            let error = params['error'];

            this.treatsError(error);
            this.treatsLogin(token);
        });

    }

    private treatsLogin(token: String) {
        if (token == null) {
            this.router.navigate(['/']);
        } else {
            this.authService.saveCredentials(token, '', false)
        }
    }

    private treatsError(message: String) {
        if (message != null) {
            message.replace('%20', ' ');
            message.replace('%27', '\'');

            this.toaster.error(message.toString(), 'Error');
            this.router.navigate(['/']);
        }
    }

}
