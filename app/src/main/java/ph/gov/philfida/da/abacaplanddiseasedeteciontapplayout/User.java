/* Copyright 2019 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/
package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout;

public class User {
    public String lastName, middleName, firstName, birthday, email, permanentAddress, occupation, institution;

    public User() {

    }

    public User(String lastName, String middleName, String firstName, String birthday,
                String email, String permanentAddress, String occupation, String institution) {
        this.lastName = lastName;
        this.middleName = middleName;
        this.firstName = firstName;
        this.birthday = birthday;
        this.email = email;
        this.permanentAddress = permanentAddress;
        this.occupation = occupation;
        this.institution = institution;
    }
}
