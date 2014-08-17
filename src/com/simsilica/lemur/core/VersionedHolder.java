/*
 * $Id: VersionedHolder.java 1053 2013-07-26 10:22:40Z PSpeed42@gmail.com $
 *
 * Copyright (c) 2012-2012 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.simsilica.lemur.core;


/**
 *  A utility implementation of a simple VersionedObject wrapper
 *  object.  This provides easy support for a VersionedObject especially
 *  in cases where a referring class will have several  separately
 *  versioned values.
 *
 *  @author    Paul Speed
 */
public class VersionedHolder<T> implements VersionedObject<T> {

    private T value;
    private long version;

    public VersionedHolder() {
    }

    public VersionedHolder(T initialValue) {
        this.value = initialValue;
    }

    public long getVersion() {
        return version;
    }

    public void setObject( T value ) {
        this.value = value;
        incrementVersion();
    }

    public boolean updateObject( T value ) {
        if( this.value == value )
            return false;
        if( this.value != null && this.value.equals(value) )
            return false;
        setObject(value);
        return true;
    }

    public void incrementVersion() {
        version++;
    }

    public T getObject() {
        return value;
    }

    public VersionedReference<T> createReference() {
        return new VersionedReference<T>(this);
    }
}
