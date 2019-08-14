import {Component, OnInit} from '@angular/core';
import {AuthenticationService} from "../service/authentication.service";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
    selector: 'app-oauth2',
    templateUrl: './oauth2.component.html',
    styleUrls: ['./oauth2.component.css']
})
export class Oauth2Component implements OnInit {

    constructor(private router: Router,
                private activatedRoute: ActivatedRoute,
                private authService: AuthenticationService) {
    }

    ngOnInit() {

        if (AuthenticationService.isUserLogin()) {
            this.router.navigate(['/statistics']);
        }

        this.activatedRoute.queryParams.subscribe(params => {
            let token = params['token'];

            if (token == null) {
                this.router.navigate(['/']);
            } else {
                this.authService.saveCredentials(token, '', false)
            }
        });

    }

}
