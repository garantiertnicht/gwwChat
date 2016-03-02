/*
 * Copyright (c) 2016, garantiertnicht
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *    This product includes software developed by garantiertnicht Weichware.
 * 4. Neither the name of garantiertnicht Weichware nor the
 *    names of its contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GARANTIERTNICHT WEICHWARE ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL GARANTIERTNICHT WEICHWARE BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package de.garantiertnicht.Weichware.gwwChat;

import org.simmetrics.metrics.JaroWinkler;

public class FilterList {
    private static String[] blacks = {"gari", "gary", "martin", "marcel", "harry", "nottingham", "garantiertmel", "girls", "garantiertmarcel", "cel"};

    public static boolean isValidMsg(String msg) {
        msg = msg.toLowerCase();

        JaroWinkler alg = new JaroWinkler();

        for(String s: msg.split(" ")) {
            if(alg.distance(s, "garantiertnicht") < 0.1)
                continue;
            if(s.equals("garantierthi"))
                continue;
            if(alg.distance(s, "gara") < 0.1)
                return false;
            for(String b : blacks)
                if(alg.distance(s, b) < 0.1)
                    return false;
        }

        String msg1 = "";
        for(char a : msg.toCharArray())
            if(Character.isLetter(a) || a == ' ')
                msg1 += a;

        for(String s : msg.split(" ")) {
            if(alg.distance(s, "garantiertnicht") < 0.1)
                continue;
            if(s.equals("garantierthi"))
                continue;
            if(alg.distance(s, "gara") < 0.1)
                return false;
            for(String b : blacks)
                if(alg.distance(s, b) < 0.1)
                    return false;
        }

        msg1 = "";
        for(char a : msg.toCharArray())
            if(Character.isLetter(a))
                msg1 += a;

        if(!msg1.contains("garantiertnicht"))
            for(String b : blacks)
                if (msg1.contains(b))
                    return false;

        return true;
    }
}
