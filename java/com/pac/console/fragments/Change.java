/*
 * Copyright (C) 2016 The PAC-ROM Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pac.console.fragments;

/**
 * Created by Aaron Kable on 2016-08-15.
 */
public class Change implements Comparable<Change> {

    // Ch ch ch Chaaaaanges!!!
    private int mDate;
    private String mHash;
    private String mProject;
    private String mCommitText;
    private String mCommitAuth;

    public Change(int dt, String Project, String Hash, String Text, String Auth){
        this.mDate = dt;
        this.mProject = Project;
        this.mHash = Hash;
        this.mCommitText = Text;
        this.mCommitAuth = Auth;
    }

    public String getCommitAuth() {
        return mCommitAuth;
    }

    public void setCommitAuth(String mCommitAuth) {
        this.mCommitAuth = mCommitAuth;
    }

    public String getCommitText() {
        return mCommitText;
    }

    public void setCommitText(String mCommitText) {
        this.mCommitText = mCommitText;
    }

    public String getHash() {
        return mHash;
    }

    public void setHash(String mHash) {
        this.mHash = mHash;
    }

    public int getmDate() {
        return mDate;
    }

    public void setmDate(int mDate) {
        this.mDate = mDate;
    }

    public String getmProject() {
        return mProject;
    }

    public void setmProject(String mProject) {
        this.mProject = mProject;
    }

    public int compareTo(Change another) {
        int comp_date = another.mDate;
        if (this.mDate > comp_date){
            return -1;
        } else if (this.mDate < comp_date){
            return 1;
        } else {
            return 0;
        }
    }

}
