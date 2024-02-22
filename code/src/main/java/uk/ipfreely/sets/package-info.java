/**
 * <p>
 *     Specialized IP address collection types capable of representing entire address ranges or subsets thereof.
 *     Use {@link uk.ipfreely.sets.AddressSets} to create sets.
 * </p>
 * <table border="1">
 *     <caption>AddressSet Contracts</caption>
 *     <tr>
 *         <th>
 *             Interface
 *         </th>
 *         <th>
 *             Must Be Implemented When Set...
 *         </th>
 *     </tr>
 *     <tr>
 *         <td>
 *             {@link uk.ipfreely.sets.Block}
 *         </td>
 *         <td>
 *             ...forms a valid
 *             <a href="https://tools.ietf.org/html/rfc4632">RFC4632</a>
 *             CIDR block.
 *         </td>
 *     </tr>
 *     <tr>
 *         <td>
 *             {@link uk.ipfreely.sets.Range}
 *         </td>
 *         <td>
 *             ...is contiguous range.
 *         </td>
 *     </tr>
 *     <tr>
 *         <td>
 *             {@link uk.ipfreely.sets.AddressSet}
 *         </td>
 *         <td>
 *             ...is made up of non-contiguous ranges or is the empty set.
 *         </td>
 *     </tr>
 * </table>
 */
package uk.ipfreely.sets;
