import {Component, OnInit} from '@angular/core';
import {AuthenticationService} from "../service/authentication.service";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
    selector: 'app-verification',
    templateUrl: './verification.component.html',
    styleUrls: ['./verification.component.css']
})
export class VerificationComponent implements OnInit {

    public message: String;
    public isError: boolean;

    constructor(private authService: AuthenticationService,
                private router: Router,
                private activatedRoute: ActivatedRoute) {
    }

    ngOnInit() {
        if (this.authService.isUserLogin()) {
            this.router.navigate(['/statistics']);
        }

        this.activatedRoute.queryParams.subscribe(params => {
            let token = params['token'];

            if (token == null) {
                this.router.navigate(['/']);
            } else {
                this.verifyEmail(token)
            }
        });
    }

    private verifyEmail(token: String) {
        this.authService.verifyEmail(token).subscribe(response => {
                this.message = response.message;
                this.isError = false
            },
            error => {
                this.message = error;
                this.isError = true;
            }
        )
    }

}
