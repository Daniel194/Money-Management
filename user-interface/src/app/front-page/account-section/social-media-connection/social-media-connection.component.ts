import {animate, state, style, transition, trigger} from "@angular/animations";
import {AccountSection} from "../account-section";
import {ChangeDetectorRef, Component} from "@angular/core";
import {ToastrService} from "ngx-toastr";
import {AuthenticationService} from "../../../service/authentication.service";

@Component({
    selector: 'app-social-media-connection',
    templateUrl: './social-media-connection.component.html',
    styleUrls: ['./social-media-connection.component.css'],
    animations: [
        trigger('showSocial', [
            state('true', style({opacity: 1, transform: 'translateY(0%)'})),
            state('false', style({opacity: 0, transform: 'translateY(25%)'})),
            transition('false => true', animate('1s 2s ease-in'))
        ])
    ]
})
export class SocialMediaConnectionComponent extends AccountSection{


    constructor(cdr: ChangeDetectorRef,
                toaster: ToastrService,
                private auth : AuthenticationService) {
        super(cdr, toaster);
    }

    socialConnection(provide : String) {
        this.auth.socialMediaConnection("google")
            .subscribe(() => this.displaySuccessMessage("Redirect !"))
    }
}
