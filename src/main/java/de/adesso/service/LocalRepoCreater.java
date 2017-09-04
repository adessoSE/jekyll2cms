package de.adesso.service;

import org.eclipse.jgit.api.Git;

public abstract class LocalRepoCreater {

    private static Git localGit;

    static Git getLocalGit() {

        return localGit;
    }

    static void setLocalGit(Git local) {

        localGit = local;
    }
}
