/*
 * Copyright 2011-2013 UnboundID Corp.
 * All Rights Reserved.
 */
/*
 * Copyright (C) 2011-2013 UnboundID Corp.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License (GPLv2 only)
 * or the terms of the GNU Lesser General Public License (LGPLv2.1 only)
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 */
package com.hwlcn.ldap.ldap.listener;



import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.hwlcn.ldap.ldap.protocol.LDAPMessage;
import com.hwlcn.ldap.ldap.sdk.Control;
import com.hwlcn.ldap.ldap.sdk.LDAPException;
import com.hwlcn.ldap.ldap.sdk.ResultCode;
import com.hwlcn.ldap.ldap.sdk.controls.AssertionRequestControl;
import com.hwlcn.ldap.ldap.sdk.controls.AuthorizationIdentityRequestControl;
import com.hwlcn.ldap.ldap.sdk.controls.DontUseCopyRequestControl;
import com.hwlcn.ldap.ldap.sdk.controls.ManageDsaITRequestControl;
import com.hwlcn.ldap.ldap.sdk.controls.PermissiveModifyRequestControl;
import com.hwlcn.ldap.ldap.sdk.controls.PostReadRequestControl;
import com.hwlcn.ldap.ldap.sdk.controls.PreReadRequestControl;
import com.hwlcn.ldap.ldap.sdk.controls.ProxiedAuthorizationV1RequestControl;
import com.hwlcn.ldap.ldap.sdk.controls.ProxiedAuthorizationV2RequestControl;
import com.hwlcn.ldap.ldap.sdk.controls.ServerSideSortRequestControl;
import com.hwlcn.ldap.ldap.sdk.controls.SimplePagedResultsControl;
import com.hwlcn.ldap.ldap.sdk.controls.SubentriesRequestControl;
import com.hwlcn.ldap.ldap.sdk.controls.SubtreeDeleteRequestControl;
import com.hwlcn.ldap.ldap.sdk.controls.TransactionSpecificationRequestControl;
import com.hwlcn.ldap.ldap.sdk.controls.VirtualListViewRequestControl;

import static com.hwlcn.ldap.ldap.listener.ListenerMessages.*;



/**
 * This class provides a mechanism for pre-processing request controls.  It will
 * decode each of the controls to a more appropriate type, ensure that they are
 * acceptable for the type of operation for which they have been requested, and
 * organize them by request OID.  Any unrecognized critical controls will result
 * in an exception.  Any unrecognized non-critical controls will be ignored.
 * <BR><BR>
 * This class is only intended to be used in conjunction with the in-memory
 * request processor.
 */
final class RequestControlPreProcessor
{
  /**
   * Performs the appropriate processing for the given set of controls.
   *
   * @param  requestOpType  The protocol op type for the request in which the
   *                        controls were received.
   * @param  controls       The list of controls included in the client request.
   *
   * @return  A map containing the controls sorted by OID.  They will have been
   *          converted to a more specific object type.
   *
   * @throws  com.hwlcn.ldap.ldap.sdk.LDAPException  If a problem is encountered while processing the
   *                         provided set of controls.
   */
  static Map<String,Control> processControls(final byte requestOpType,
                                             final List<Control> controls)
         throws LDAPException
  {
    final Map<String,Control> m =
         new LinkedHashMap<String,Control>(controls.size());

    for (final Control control : controls)
    {
      final String oid = control.getOID();
      if (oid.equals(AssertionRequestControl.ASSERTION_REQUEST_OID))
      {
        switch (requestOpType)
        {
          case LDAPMessage.PROTOCOL_OP_TYPE_ADD_REQUEST:
          case LDAPMessage.PROTOCOL_OP_TYPE_COMPARE_REQUEST:
          case LDAPMessage.PROTOCOL_OP_TYPE_DELETE_REQUEST:
          case LDAPMessage.PROTOCOL_OP_TYPE_MODIFY_REQUEST:
          case LDAPMessage.PROTOCOL_OP_TYPE_MODIFY_DN_REQUEST:
          case LDAPMessage.PROTOCOL_OP_TYPE_SEARCH_REQUEST:
            // The control is acceptable for these operations.
            break;

          default:
            if (control.isCritical())
            {
              throw new LDAPException(ResultCode.UNAVAILABLE_CRITICAL_EXTENSION,
                   ERR_CONTROL_PROCESSOR_UNSUPPORTED_FOR_OP.get(oid));
            }
            else
            {
              continue;
            }
        }

        if (m.put(oid, new AssertionRequestControl(control)) != null)
        {
          throw new LDAPException(ResultCode.CONSTRAINT_VIOLATION,
               ERR_CONTROL_PROCESSOR_MULTIPLE_CONTROLS.get(oid));
        }
      }
      else if (oid.equals(AuthorizationIdentityRequestControl.
           AUTHORIZATION_IDENTITY_REQUEST_OID))
      {
        switch (requestOpType)
        {
          case LDAPMessage.PROTOCOL_OP_TYPE_BIND_REQUEST:
            // The control is acceptable for these operations.
            break;

          default:
            if (control.isCritical())
            {
              throw new LDAPException(ResultCode.UNAVAILABLE_CRITICAL_EXTENSION,
                   ERR_CONTROL_PROCESSOR_UNSUPPORTED_FOR_OP.get(oid));
            }
            else
            {
              continue;
            }
        }

        if (m.put(oid, new AuthorizationIdentityRequestControl(control)) !=
             null)
        {
          throw new LDAPException(ResultCode.CONSTRAINT_VIOLATION,
               ERR_CONTROL_PROCESSOR_MULTIPLE_CONTROLS.get(oid));
        }
      }
      else if (oid.equals(DontUseCopyRequestControl.DONT_USE_COPY_REQUEST_OID))
      {
        switch (requestOpType)
        {
          case LDAPMessage.PROTOCOL_OP_TYPE_COMPARE_REQUEST:
          case LDAPMessage.PROTOCOL_OP_TYPE_SEARCH_REQUEST:
            // The control is acceptable for these operations.
            break;

          default:
            if (control.isCritical())
            {
              throw new LDAPException(ResultCode.UNAVAILABLE_CRITICAL_EXTENSION,
                   ERR_CONTROL_PROCESSOR_UNSUPPORTED_FOR_OP.get(oid));
            }
            else
            {
              continue;
            }
        }

        if (m.put(oid, new DontUseCopyRequestControl(control)) != null)
        {
          throw new LDAPException(ResultCode.CONSTRAINT_VIOLATION,
               ERR_CONTROL_PROCESSOR_MULTIPLE_CONTROLS.get(oid));
        }
      }
      else if (oid.equals(ManageDsaITRequestControl.MANAGE_DSA_IT_REQUEST_OID))
      {
        switch (requestOpType)
        {
          case LDAPMessage.PROTOCOL_OP_TYPE_ADD_REQUEST:
          case LDAPMessage.PROTOCOL_OP_TYPE_COMPARE_REQUEST:
          case LDAPMessage.PROTOCOL_OP_TYPE_DELETE_REQUEST:
          case LDAPMessage.PROTOCOL_OP_TYPE_MODIFY_REQUEST:
          case LDAPMessage.PROTOCOL_OP_TYPE_MODIFY_DN_REQUEST:
          case LDAPMessage.PROTOCOL_OP_TYPE_SEARCH_REQUEST:
            // The control is acceptable for these operations.
            break;

          default:
            if (control.isCritical())
            {
              throw new LDAPException(ResultCode.UNAVAILABLE_CRITICAL_EXTENSION,
                   ERR_CONTROL_PROCESSOR_UNSUPPORTED_FOR_OP.get(oid));
            }
            else
            {
              continue;
            }
        }

        if (m.put(oid, new ManageDsaITRequestControl(control)) != null)
        {
          throw new LDAPException(ResultCode.CONSTRAINT_VIOLATION,
               ERR_CONTROL_PROCESSOR_MULTIPLE_CONTROLS.get(oid));
        }
      }
      else if (oid.equals(PermissiveModifyRequestControl.
           PERMISSIVE_MODIFY_REQUEST_OID))
      {
        switch (requestOpType)
        {
          case LDAPMessage.PROTOCOL_OP_TYPE_MODIFY_REQUEST:
            // The control is acceptable for these operations.
            break;

          default:
            if (control.isCritical())
            {
              throw new LDAPException(ResultCode.UNAVAILABLE_CRITICAL_EXTENSION,
                   ERR_CONTROL_PROCESSOR_UNSUPPORTED_FOR_OP.get(oid));
            }
            else
            {
              continue;
            }
        }

        if (m.put(oid, new PermissiveModifyRequestControl(control)) != null)
        {
          throw new LDAPException(ResultCode.CONSTRAINT_VIOLATION,
               ERR_CONTROL_PROCESSOR_MULTIPLE_CONTROLS.get(oid));
        }
      }
      else if (oid.equals(PostReadRequestControl.POST_READ_REQUEST_OID))
      {
        switch (requestOpType)
        {
          case LDAPMessage.PROTOCOL_OP_TYPE_ADD_REQUEST:
          case LDAPMessage.PROTOCOL_OP_TYPE_MODIFY_REQUEST:
          case LDAPMessage.PROTOCOL_OP_TYPE_MODIFY_DN_REQUEST:
            // The control is acceptable for these operations.
            break;

          default:
            if (control.isCritical())
            {
              throw new LDAPException(ResultCode.UNAVAILABLE_CRITICAL_EXTENSION,
                   ERR_CONTROL_PROCESSOR_UNSUPPORTED_FOR_OP.get(oid));
            }
            else
            {
              continue;
            }
        }

        if (m.put(oid, new PostReadRequestControl(control)) != null)
        {
          throw new LDAPException(ResultCode.CONSTRAINT_VIOLATION,
               ERR_CONTROL_PROCESSOR_MULTIPLE_CONTROLS.get(oid));
        }
      }
      else if (oid.equals(PreReadRequestControl.PRE_READ_REQUEST_OID))
      {
        switch (requestOpType)
        {
          case LDAPMessage.PROTOCOL_OP_TYPE_DELETE_REQUEST:
          case LDAPMessage.PROTOCOL_OP_TYPE_MODIFY_REQUEST:
          case LDAPMessage.PROTOCOL_OP_TYPE_MODIFY_DN_REQUEST:
            // The control is acceptable for these operations.
            break;

          default:
            if (control.isCritical())
            {
              throw new LDAPException(ResultCode.UNAVAILABLE_CRITICAL_EXTENSION,
                   ERR_CONTROL_PROCESSOR_UNSUPPORTED_FOR_OP.get(oid));
            }
            else
            {
              continue;
            }
        }

        if (m.put(oid, new PreReadRequestControl(control)) != null)
        {
          throw new LDAPException(ResultCode.CONSTRAINT_VIOLATION,
               ERR_CONTROL_PROCESSOR_MULTIPLE_CONTROLS.get(oid));
        }
      }
      else if (oid.equals(ProxiedAuthorizationV1RequestControl.
           PROXIED_AUTHORIZATION_V1_REQUEST_OID))
      {
        switch (requestOpType)
        {
          case LDAPMessage.PROTOCOL_OP_TYPE_ADD_REQUEST:
          case LDAPMessage.PROTOCOL_OP_TYPE_COMPARE_REQUEST:
          case LDAPMessage.PROTOCOL_OP_TYPE_DELETE_REQUEST:
          case LDAPMessage.PROTOCOL_OP_TYPE_MODIFY_REQUEST:
          case LDAPMessage.PROTOCOL_OP_TYPE_MODIFY_DN_REQUEST:
          case LDAPMessage.PROTOCOL_OP_TYPE_SEARCH_REQUEST:
            // The control is acceptable for these operations.
            break;

          default:
            if (control.isCritical())
            {
              throw new LDAPException(ResultCode.UNAVAILABLE_CRITICAL_EXTENSION,
                   ERR_CONTROL_PROCESSOR_UNSUPPORTED_FOR_OP.get(oid));
            }
            else
            {
              continue;
            }
        }

        if (m.put(oid, new ProxiedAuthorizationV1RequestControl(control)) !=
             null)
        {
          throw new LDAPException(ResultCode.CONSTRAINT_VIOLATION,
               ERR_CONTROL_PROCESSOR_MULTIPLE_CONTROLS.get(oid));
        }
      }
      else if (oid.equals(ProxiedAuthorizationV2RequestControl.
           PROXIED_AUTHORIZATION_V2_REQUEST_OID))
      {
        switch (requestOpType)
        {
          case LDAPMessage.PROTOCOL_OP_TYPE_ADD_REQUEST:
          case LDAPMessage.PROTOCOL_OP_TYPE_COMPARE_REQUEST:
          case LDAPMessage.PROTOCOL_OP_TYPE_DELETE_REQUEST:
          case LDAPMessage.PROTOCOL_OP_TYPE_MODIFY_REQUEST:
          case LDAPMessage.PROTOCOL_OP_TYPE_MODIFY_DN_REQUEST:
          case LDAPMessage.PROTOCOL_OP_TYPE_SEARCH_REQUEST:
            // The control is acceptable for these operations.
            break;

          default:
            if (control.isCritical())
            {
              throw new LDAPException(ResultCode.UNAVAILABLE_CRITICAL_EXTENSION,
                   ERR_CONTROL_PROCESSOR_UNSUPPORTED_FOR_OP.get(oid));
            }
            else
            {
              continue;
            }
        }

        if (m.put(oid, new ProxiedAuthorizationV2RequestControl(control)) !=
             null)
        {
          throw new LDAPException(ResultCode.CONSTRAINT_VIOLATION,
               ERR_CONTROL_PROCESSOR_MULTIPLE_CONTROLS.get(oid));
        }
      }
      else if (oid.equals(ServerSideSortRequestControl.
           SERVER_SIDE_SORT_REQUEST_OID))
      {
        switch (requestOpType)
        {
          case LDAPMessage.PROTOCOL_OP_TYPE_SEARCH_REQUEST:
            // The control is acceptable for these operations.
            break;

          default:
            if (control.isCritical())
            {
              throw new LDAPException(ResultCode.UNAVAILABLE_CRITICAL_EXTENSION,
                   ERR_CONTROL_PROCESSOR_UNSUPPORTED_FOR_OP.get(oid));
            }
            else
            {
              continue;
            }
        }

        if (m.put(oid, new ServerSideSortRequestControl(control)) != null)
        {
          throw new LDAPException(ResultCode.CONSTRAINT_VIOLATION,
               ERR_CONTROL_PROCESSOR_MULTIPLE_CONTROLS.get(oid));
        }
      }
      else if (oid.equals(SimplePagedResultsControl.PAGED_RESULTS_OID))
      {
        switch (requestOpType)
        {
          case LDAPMessage.PROTOCOL_OP_TYPE_SEARCH_REQUEST:
            // The control is acceptable for these operations.
            break;

          default:
            if (control.isCritical())
            {
              throw new LDAPException(ResultCode.UNAVAILABLE_CRITICAL_EXTENSION,
                   ERR_CONTROL_PROCESSOR_UNSUPPORTED_FOR_OP.get(oid));
            }
            else
            {
              continue;
            }
        }

        if (m.put(oid, new SimplePagedResultsControl(control.getOID(),
             control.isCritical(), control.getValue())) != null)
        {
          throw new LDAPException(ResultCode.CONSTRAINT_VIOLATION,
               ERR_CONTROL_PROCESSOR_MULTIPLE_CONTROLS.get(oid));
        }
      }
      else if (oid.equals(SubentriesRequestControl.SUBENTRIES_REQUEST_OID))
      {
        switch (requestOpType)
        {
          case LDAPMessage.PROTOCOL_OP_TYPE_SEARCH_REQUEST:
            // The control is acceptable for these operations.
            break;

          default:
            if (control.isCritical())
            {
              throw new LDAPException(ResultCode.UNAVAILABLE_CRITICAL_EXTENSION,
                   ERR_CONTROL_PROCESSOR_UNSUPPORTED_FOR_OP.get(oid));
            }
            else
            {
              continue;
            }
        }

        if (m.put(oid, new SubentriesRequestControl(control)) != null)
        {
          throw new LDAPException(ResultCode.CONSTRAINT_VIOLATION,
               ERR_CONTROL_PROCESSOR_MULTIPLE_CONTROLS.get(oid));
        }
      }
      else if (oid.equals(SubtreeDeleteRequestControl.
           SUBTREE_DELETE_REQUEST_OID))
      {
        switch (requestOpType)
        {
          case LDAPMessage.PROTOCOL_OP_TYPE_DELETE_REQUEST:
            // The control is acceptable for these operations.
            break;

          default:
            if (control.isCritical())
            {
              throw new LDAPException(ResultCode.UNAVAILABLE_CRITICAL_EXTENSION,
                   ERR_CONTROL_PROCESSOR_UNSUPPORTED_FOR_OP.get(oid));
            }
            else
            {
              continue;
            }
        }

        if (m.put(oid, new SubtreeDeleteRequestControl(control)) != null)
        {
          throw new LDAPException(ResultCode.CONSTRAINT_VIOLATION,
               ERR_CONTROL_PROCESSOR_MULTIPLE_CONTROLS.get(oid));
        }
      }
      else if (oid.equals(TransactionSpecificationRequestControl.
           TRANSACTION_SPECIFICATION_REQUEST_OID))
      {
        switch (requestOpType)
        {
          case LDAPMessage.PROTOCOL_OP_TYPE_ADD_REQUEST:
          case LDAPMessage.PROTOCOL_OP_TYPE_DELETE_REQUEST:
          case LDAPMessage.PROTOCOL_OP_TYPE_MODIFY_REQUEST:
          case LDAPMessage.PROTOCOL_OP_TYPE_MODIFY_DN_REQUEST:
            // The control is acceptable for these operations.
            break;

          default:
            if (control.isCritical())
            {
              throw new LDAPException(ResultCode.UNAVAILABLE_CRITICAL_EXTENSION,
                   ERR_CONTROL_PROCESSOR_UNSUPPORTED_FOR_OP.get(oid));
            }
            else
            {
              continue;
            }
        }

        if (m.put(oid, new TransactionSpecificationRequestControl(control)) !=
             null)
        {
          throw new LDAPException(ResultCode.CONSTRAINT_VIOLATION,
               ERR_CONTROL_PROCESSOR_MULTIPLE_CONTROLS.get(oid));
        }
      }
      else if (oid.equals(VirtualListViewRequestControl.
           VIRTUAL_LIST_VIEW_REQUEST_OID))
      {
        switch (requestOpType)
        {
          case LDAPMessage.PROTOCOL_OP_TYPE_SEARCH_REQUEST:
            // The control is acceptable for these operations.
            break;

          default:
            if (control.isCritical())
            {
              throw new LDAPException(ResultCode.UNAVAILABLE_CRITICAL_EXTENSION,
                   ERR_CONTROL_PROCESSOR_UNSUPPORTED_FOR_OP.get(oid));
            }
            else
            {
              continue;
            }
        }

        if (m.put(oid, new VirtualListViewRequestControl(control)) != null)
        {
          throw new LDAPException(ResultCode.CONSTRAINT_VIOLATION,
               ERR_CONTROL_PROCESSOR_MULTIPLE_CONTROLS.get(oid));
        }
      }
      else if (oid.equals(InMemoryRequestHandler.
           OID_INTERNAL_OPERATION_REQUEST_CONTROL))
      {
        // This control will always be allowed.
        m.put(oid, control);
      }
      else if (control.isCritical())
      {
        throw new LDAPException(ResultCode.UNAVAILABLE_CRITICAL_EXTENSION,
             ERR_CONTROL_PROCESSOR_UNSUPPORTED_CONTROL.get(oid));
      }
    }

    if (m.containsKey(ProxiedAuthorizationV1RequestControl.
             PROXIED_AUTHORIZATION_V1_REQUEST_OID) &&
        m.containsKey(ProxiedAuthorizationV2RequestControl.
             PROXIED_AUTHORIZATION_V2_REQUEST_OID))
    {
      throw new LDAPException(ResultCode.CONSTRAINT_VIOLATION,
           ERR_CONTROL_PROCESSOR_MULTIPLE_PROXY_CONTROLS.get());
    }

    if (m.containsKey(
             VirtualListViewRequestControl.VIRTUAL_LIST_VIEW_REQUEST_OID))
    {
      if (m.containsKey(SimplePagedResultsControl.PAGED_RESULTS_OID))
      {
        throw new LDAPException(ResultCode.CONSTRAINT_VIOLATION,
             ERR_CONTROL_PROCESSOR_VLV_AND_PAGED_RESULTS.get());
      }

      if (! m.containsKey(
                 ServerSideSortRequestControl.SERVER_SIDE_SORT_REQUEST_OID))
      {
        throw new LDAPException(ResultCode.SORT_CONTROL_MISSING,
             ERR_CONTROL_PROCESSOR_VLV_WITHOUT_SORT.get());
      }
    }

    return m;
  }
}
